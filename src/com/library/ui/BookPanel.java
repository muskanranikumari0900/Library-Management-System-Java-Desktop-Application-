package com.library.ui;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookPanel extends JPanel {
    private JTextField txtId, txtTitle, txtAuthor, txtIsbn, txtSearch;
    private JCheckBox chkAvailable;
    private JTable table;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;

    public BookPanel() {
        bookDAO = new BookDAO();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initUI();
        loadBooks();
    }

    private void initUI() {
        // Title
        JLabel lblTitle = new JLabel("Manage Books");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(lblTitle, BorderLayout.NORTH);

        // Center Content (Form + Table)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 20, 10, 20),
                BorderFactory.createTitledBorder("Book Details")
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(10);
        txtId.setEnabled(false);
        txtTitle = new JTextField(20);
        txtAuthor = new JTextField(20);
        txtIsbn = new JTextField(15);
        chkAvailable = new JCheckBox("Available");
        chkAvailable.setBackground(Color.WHITE);
        chkAvailable.setSelected(true);

        UIUtils.styleTextField(txtId);
        UIUtils.styleTextField(txtTitle);
        UIUtils.styleTextField(txtAuthor);
        UIUtils.styleTextField(txtIsbn);

        addFormField(formPanel, "Book ID (Auto):", txtId, gbc, 0, 0);
        addFormField(formPanel, "Title:", txtTitle, gbc, 0, 1);
        addFormField(formPanel, "Author:", txtAuthor, gbc, 0, 2);
        addFormField(formPanel, "ISBN:", txtIsbn, gbc, 0, 3);
        
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(chkAvailable, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        UIUtils.styleButton(btnAdd);
        UIUtils.styleButton(btnUpdate);
        UIUtils.styleButton(btnDelete);
        UIUtils.styleButton(btnClear);

        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(buttonPanel, gbc);

        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        txtSearch = new JTextField(20);
        UIUtils.styleTextField(txtSearch);
        JButton btnSearch = new JButton("Search");
        UIUtils.styleButton(btnSearch);
        btnSearch.addActionListener(e -> searchBooks());
        
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        tablePanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Title", "Author", "ISBN", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UIUtils.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);

        // Table Selection Listner
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtId.setText(table.getValueAt(row, 0).toString());
                txtTitle.setText(table.getValueAt(row, 1).toString());
                txtAuthor.setText(table.getValueAt(row, 2).toString());
                txtIsbn.setText(table.getValueAt(row, 3).toString());
                chkAvailable.setSelected(table.getValueAt(row, 4).toString().equals("Yes"));
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

    private void loadBooks() {
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.getAllBooks();
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.isAvailable() ? "Yes" : "No"});
        }
    }

    private void searchBooks() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.searchBooks(keyword);
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.isAvailable() ? "Yes" : "No"});
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtIsbn.setText("");
        chkAvailable.setSelected(true);
        table.clearSelection();
    }

    private void addBook() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String isbn = txtIsbn.getText().trim();

        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Author are required.");
            return;
        }

        Book book = new Book(0, title, author, isbn, chkAvailable.isSelected());
        if (bookDAO.addBook(book)) {
            JOptionPane.showMessageDialog(this, "Book added successfully.");
            loadBooks();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add book.");
        }
    }

    private void updateBook() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a book to update.");
            return;
        }
        
        int id = Integer.parseInt(txtId.getText());
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String isbn = txtIsbn.getText().trim();

        Book book = new Book(id, title, author, isbn, chkAvailable.isSelected());
        if (bookDAO.updateBook(book)) {
            JOptionPane.showMessageDialog(this, "Book updated successfully.");
            loadBooks();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update book.");
        }
    }

    private void deleteBook() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a book to delete.");
            return;
        }
        
        int id = Integer.parseInt(txtId.getText());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook(id)) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully.");
                loadBooks();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete book.");
            }
        }
    }
}
