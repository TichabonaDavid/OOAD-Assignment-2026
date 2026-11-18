package com.bank.dao;
import java.sql.*;

public class AccountDAO {
    public double getBalance(String accountNumber) {
        String query = "SELECT balance FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void updateBalance(String accountNumber, double newBalance) {
        String query = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDouble(1, newBalance); pstmt.setString(2, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void recordTransaction(String accountNumber, String type, double amount) {
        String query = "INSERT INTO transactions (account_number, type, amount) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, accountNumber); pstmt.setString(2, type); pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}