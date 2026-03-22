package com.library.dao;

import com.library.model.Issue;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueDAO {

    public boolean issueBook(int bookId, int memberId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false); // Transaction start

            // Insert into issues
            String issueQuery = "INSERT INTO issues (book_id, member_id, issue_date) VALUES (?, ?, CURDATE())";
            try (PreparedStatement stmt1 = conn.prepareStatement(issueQuery)) {
                stmt1.setInt(1, bookId);
                stmt1.setInt(2, memberId);
                stmt1.executeUpdate();
            }

            // Update book availability
            String updateBookQuery = "UPDATE books SET available = FALSE WHERE id = ?";
            try (PreparedStatement stmt2 = conn.prepareStatement(updateBookQuery)) {
                stmt2.setInt(1, bookId);
                stmt2.executeUpdate();
            }

            conn.commit(); // Transaction end
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public boolean returnBook(int issueId, int bookId) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try {
            conn.setAutoCommit(false);

            // Update return date
            String updateIssueQuery = "UPDATE issues SET return_date = CURDATE() WHERE id = ?";
            try (PreparedStatement stmt1 = conn.prepareStatement(updateIssueQuery)) {
                stmt1.setInt(1, issueId);
                stmt1.executeUpdate();
            }

            // Update book availability
            String updateBookQuery = "UPDATE books SET available = TRUE WHERE id = ?";
            try (PreparedStatement stmt2 = conn.prepareStatement(updateBookQuery)) {
                stmt2.setInt(1, bookId);
                stmt2.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public List<Issue> getAllIssues() {
        List<Issue> issues = new ArrayList<>();
        String query = "SELECT i.*, b.title as book_title, m.name as member_name " +
                       "FROM issues i " +
                       "JOIN books b ON i.book_id = b.id " +
                       "JOIN members m ON i.member_id = m.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Issue issue = new Issue();
                issue.setId(rs.getInt("id"));
                issue.setBookId(rs.getInt("book_id"));
                issue.setMemberId(rs.getInt("member_id"));
                issue.setIssueDate(rs.getDate("issue_date"));
                issue.setReturnDate(rs.getDate("return_date"));
                issue.setBookTitle(rs.getString("book_title"));
                issue.setMemberName(rs.getString("member_name"));
                issues.add(issue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return issues;
    }
}
