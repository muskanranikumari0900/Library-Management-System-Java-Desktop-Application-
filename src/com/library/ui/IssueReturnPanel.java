package com.library.ui;

import com.library.dao.BookDAO;
import com.library.dao.IssueDAO;
import com.library.dao.MemberDAO;
import com.library.model.Book;
import com.library.model.Issue;
import com.library.model.Member;
import com.library.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IssueReturnPanel extends JPanel {
    private JComboBox<Book> comboBooks;
    private JComboBox<Member> comboMembers;
    private JTextField txtIssueId;
    
    private JTable table;
    private DefaultTableModel tableModel;

    private BookDAO bookDAO;
    private MemberDAO memberDAO;
    private IssueDAO issueDAO;

    public IssueReturnPanel() {
        bookDAO = new BookDAO();
        memberDAO = new MemberDAO();
        issueDAO = new IssueDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initUI();
        loadIssues();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("Issue & Return Books");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 20, 10, 20),
                BorderFactory.createTitledBorder("Issue / Return Details")
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboBooks = new JComboBox<>();
        comboMembers = new JComboBox<>();
        txtIssueId = new JTextField(10);
        txtIssueId.setEnabled(false);

        loadDropdowns();

        addFormField(formPanel, "Select Member:", comboMembers, gbc, 0, 0);
        addFormField(formPanel, "Select Book:", comboBooks, gbc, 0, 1);
        addFormField(formPanel, "Issue ID (For Return):", txtIssueId, gbc, 0, 2);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnIssue = new JButton("Issue Book");
        JButton btnReturn = new JButton("Return Book");
        JButton btnRefreshData = new JButton("Refresh Data");

        UIUtils.styleButton(btnIssue);
        UIUtils.styleButton(btnReturn);
        UIUtils.styleButton(btnRefreshData);

        btnIssue.addActionListener(e -> issueBook());
        btnReturn.addActionListener(e -> returnBook());
        btnRefreshData.addActionListener(e -> {
            loadDropdowns();
            loadIssues();
            txtIssueId.setText("");
        });

        buttonPanel.add(btnIssue);
        buttonPanel.add(btnReturn);
        buttonPanel.add(btnRefreshData);

        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(buttonPanel, gbc);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Table
        String[] columns = {"Issue ID", "Book", "Member", "Issue Date", "Return Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UIUtils.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);

        // Table Listner to auto-fill issue ID for returning
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtIssueId.setText(table.getValueAt(row, 0).toString());
            }
        });

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, String labelText, Component field, GridBagConstraints gbc, int x, int y) {
        JLabel label = new JLabel(labelText);
        UIUtils.styleLabel(label);
        gbc.gridx = x; gbc.gridy = y;
        panel.add(label, gbc);
        gbc.gridx = x + 1;
        panel.add(field, gbc);
    }

    public void loadDropdowns() {
        comboBooks.removeAllItems();
        comboMembers.removeAllItems();
        
        // Load only available books
        List<Book> books = bookDAO.getAllBooks();
        for (Book b : books) {
            if (b.isAvailable()) {
                comboBooks.addItem(b);
            }
        }
        
        List<Member> members = memberDAO.getAllMembers();
        for (Member m : members) {
            comboMembers.addItem(m);
        }
    }

    private void loadIssues() {
        tableModel.setRowCount(0);
        List<Issue> issues = issueDAO.getAllIssues();
        for (Issue i : issues) {
            String returnDateStr = (i.getReturnDate() != null) ? i.getReturnDate().toString() : "Not Returned";
            tableModel.addRow(new Object[]{i.getId(), i.getBookTitle(), i.getMemberName(), i.getIssueDate(), returnDateStr});
        }
    }

    private void issueBook() {
        Book selectedBook = (Book) comboBooks.getSelectedItem();
        Member selectedMember = (Member) comboMembers.getSelectedItem();

        if (selectedBook == null || selectedMember == null) {
            JOptionPane.showMessageDialog(this, "Please select both a valid book and member.");
            return;
        }

        if (issueDAO.issueBook(selectedBook.getId(), selectedMember.getId())) {
            JOptionPane.showMessageDialog(this, "Book issued successfully to " + selectedMember.getName());
            loadDropdowns();
            loadIssues();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to issue book.");
        }
    }

    private void returnBook() {
        if (txtIssueId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an issue record from the table to return.");
            return;
        }
        
        int row = table.getSelectedRow();
        if (row == -1) return;
        
        String returnDateVal = tableModel.getValueAt(row, 4).toString();
        if (!returnDateVal.equals("Not Returned")) {
            JOptionPane.showMessageDialog(this, "This book has already been returned.");
            return;
        }
        
        int issueId = Integer.parseInt(txtIssueId.getText());
        
        // Find correct book ID from issue list
        int bookId = -1;
        List<Issue> issues = issueDAO.getAllIssues();
        for (Issue i : issues) {
            if (i.getId() == issueId) {
                bookId = i.getBookId();
                break;
            }
        }
        
        if (bookId != -1 && issueDAO.returnBook(issueId, bookId)) {
            JOptionPane.showMessageDialog(this, "Book returned successfully.");
            txtIssueId.setText("");
            loadDropdowns();
            loadIssues();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to return book.");
        }
    }
}
