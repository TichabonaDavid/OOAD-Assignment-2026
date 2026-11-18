package com.bank.dao;
import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:h2:mem:bankdb;DB_CLOSE_DELAY=-1";
    public static Connection getConnection() throws SQLException { return DriverManager.getConnection(URL, "sa", ""); }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50) UNIQUE, password VARCHAR(50), name VARCHAR(100))");
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (account_number VARCHAR(20) PRIMARY KEY, customer_id INT, type VARCHAR(20), balance DOUBLE)");
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (id INT PRIMARY KEY AUTO_INCREMENT, account_number VARCHAR(20), type VARCHAR(20), amount DOUBLE)");
            
            for (int i = 1; i <= 10; i++) {
                stmt.execute("INSERT INTO customers (username, password, name) VALUES ('user" + i + "', 'pass" + i + "', 'Customer " + i + "')");
            }
            stmt.execute("INSERT INTO accounts VALUES ('ACC001', 1, 'Savings', 1000.0)");
            stmt.execute("INSERT INTO accounts VALUES ('ACC002', 2, 'Investment', 5000.0)");
        } catch (SQLException e) { e.printStackTrace(); }
    }
}