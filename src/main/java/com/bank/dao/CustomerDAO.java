package com.bank.dao;
import java.sql.*;

public class CustomerDAO {
    public boolean authenticate(String username, String password) {
        String query = "SELECT * FROM customers WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username); pstmt.setString(2, password);
            return pstmt.executeQuery().next();
        } catch (SQLException e) { return false; }
    }
}