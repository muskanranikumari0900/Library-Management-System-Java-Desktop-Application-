package com.library.ui;

import com.library.dao.BookDAO;
import com.library.dao.MemberDAO;
import com.library.util.SessionManager;

import javax.swing.*;
import java.awt.*;

public class DashboardUI extends JFrame {
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public DashboardUI() {
        setTitle("Library Management System - Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        
        JLabel brandLabel = new JLabel("  Library Management System");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandLabel.setForeground(Color.WHITE);
        
        String user = SessionManager.getInstance().getCurrentUsername();
        JLabel userLabel = new JLabel("Welcome, " + (user != null ? user : "Admin") + "  ");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(Color.WHITE);

        headerPanel.add(brandLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Sidebar Navigation
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(52, 73, 94));
        sidebarPanel.setPreferredSize(new Dimension(200, 700));

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // Child Panels
        BookPanel bookPanel = new BookPanel();
        MemberPanel memberPanel = new MemberPanel();
        IssueReturnPanel issueReturnPanel = new IssueReturnPanel();
        
        JPanel homePanel = createHomePanel();

        mainContentPanel.add(homePanel, "Home");
        mainContentPanel.add(bookPanel, "Books");
        mainContentPanel.add(memberPanel, "Members");
        mainContentPanel.add(issueReturnPanel, "IssueReturn");

        // Navi Buttons
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebarPanel.add(createNavButton("Dashboard View", "Home"));
        sidebarPanel.add(createNavButton("Manage Books", "Books"));
        sidebarPanel.add(createNavButton("Manage Members", "Members"));
        sidebarPanel.add(createNavButton("Issue / Return", "IssueReturn"));
        
        sidebarPanel.add(Box.createVerticalGlue());
        
        JButton btnLogout = createNavButton("Logout", null);
        btnLogout.addActionListener(e -> {
            SessionManager.getInstance().logout();
            this.dispose();
            new LoginUI().setVisible(true);
        });
        sidebarPanel.add(btnLogout);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(44, 62, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (cardName != null) {
            button.addActionListener(e -> cardLayout.show(mainContentPanel, cardName));
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 62, 80));
            }
        });

        return button;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        BookDAO bookDAO = new BookDAO();
        MemberDAO memberDAO = new MemberDAO();
        
        int totalBooks = bookDAO.getAllBooks().size();
        int totalMembers = memberDAO.getAllMembers().size();

        JLabel lblTitle = new JLabel("System Overview");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel lblBooks = new JLabel("Total Books Registered: " + totalBooks);
        lblBooks.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        JLabel lblMembers = new JLabel("Total Members Registered: " + totalMembers);
        lblMembers.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10, 10, 30, 10);
        panel.add(lblTitle, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(lblBooks, gbc);

        gbc.gridy = 2;
        panel.add(lblMembers, gbc);

        return panel;
    }
}
