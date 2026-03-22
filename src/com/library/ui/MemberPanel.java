package com.library.ui;

import com.library.dao.MemberDAO;
import com.library.model.Member;
import com.library.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberPanel extends JPanel {
    private JTextField txtId, txtName, txtEmail, txtPhone, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private MemberDAO memberDAO;

    public MemberPanel() {
        memberDAO = new MemberDAO();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initUI();
        loadMembers();
    }

    private void initUI() {
        JLabel lblTitle = new JLabel("Manage Members");
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
                BorderFactory.createTitledBorder("Member Details")
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(10);
        txtId.setEnabled(false);
        txtName = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPhone = new JTextField(15);

        UIUtils.styleTextField(txtId);
        UIUtils.styleTextField(txtName);
        UIUtils.styleTextField(txtEmail);
        UIUtils.styleTextField(txtPhone);

        addFormField(formPanel, "Member ID (Auto):", txtId, gbc, 0, 0);
        addFormField(formPanel, "Name:", txtName, gbc, 0, 1);
        addFormField(formPanel, "Email:", txtEmail, gbc, 0, 2);
        addFormField(formPanel, "Phone:", txtPhone, gbc, 0, 3);
        
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

        btnAdd.addActionListener(e -> addMember());
        btnUpdate.addActionListener(e -> updateMember());
        btnDelete.addActionListener(e -> deleteMember());
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 1; gbc.gridy = 4;
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
        btnSearch.addActionListener(e -> searchMembers());
        
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        tablePanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Email", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        UIUtils.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);

        // Table Listner
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtId.setText(table.getValueAt(row, 0).toString());
                txtName.setText(table.getValueAt(row, 1).toString());
                txtEmail.setText(table.getValueAt(row, 2).toString());
                txtPhone.setText(table.getValueAt(row, 3).toString());
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

    private void loadMembers() {
        tableModel.setRowCount(0);
        List<Member> members = memberDAO.getAllMembers();
        for (Member m : members) {
            tableModel.addRow(new Object[]{m.getId(), m.getName(), m.getEmail(), m.getPhone()});
        }
    }

    private void searchMembers() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        List<Member> members = memberDAO.searchMembers(keyword);
        for (Member m : members) {
            tableModel.addRow(new Object[]{m.getId(), m.getName(), m.getEmail(), m.getPhone()});
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        table.clearSelection();
    }

    private void addMember() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.");
            return;
        }

        Member member = new Member(0, name, email, phone);
        if (memberDAO.addMember(member)) {
            JOptionPane.showMessageDialog(this, "Member added successfully.");
            loadMembers();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add member.");
        }
    }

    private void updateMember() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a member to update.");
            return;
        }
        
        int id = Integer.parseInt(txtId.getText());
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        Member member = new Member(id, name, email, phone);
        if (memberDAO.updateMember(member)) {
            JOptionPane.showMessageDialog(this, "Member updated successfully.");
            loadMembers();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update member.");
        }
    }

    private void deleteMember() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a member to delete.");
            return;
        }
        
        int id = Integer.parseInt(txtId.getText());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this member?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (memberDAO.deleteMember(id)) {
                JOptionPane.showMessageDialog(this, "Member deleted successfully.");
                loadMembers();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete member.");
            }
        }
    }
}
