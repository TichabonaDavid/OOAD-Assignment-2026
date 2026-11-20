package com.bank.dao;

import com.bank.model.Company;
import com.bank.model.Customer;
import com.bank.model.CustomerProfile;
import com.bank.model.Individual;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerDAO {
    public boolean authenticate(String username, String password) {
        String sql = "SELECT password_hash FROM customers WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && PasswordEncoder.matches(password, result.getString("password_hash"));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not authenticate the customer.", ex);
        }
    }

    public long create(Customer customer) {
        String sql = "INSERT INTO customers (customer_type,email,address,username,password_hash,first_name,surname,company_name,company_reg_no) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            String type = customer instanceof Company ? "COMPANY" : "INDIVIDUAL";
            statement.setString(1, type);
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getAddress());
            statement.setString(4, customer.getUsername());
            statement.setString(5, PasswordEncoder.encode(customer.getPassword()));
            if (customer instanceof Individual individual) {
                statement.setString(6, individual.getFirstname());
                statement.setString(7, individual.getSurname());
                statement.setString(8, null);
                statement.setString(9, null);
            } else if (customer instanceof Company company) {
                statement.setString(6, null);
                statement.setString(7, null);
                statement.setString(8, company.getCompanyName());
                statement.setString(9, company.getCompanyRegNo());
            } else {
                throw new IllegalArgumentException("Unsupported customer type.");
            }
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("The database did not return a customer ID.");
                return keys.getLong(1);
            }
        } catch (SQLException ex) {
            if (ex.getSQLState() != null && ex.getSQLState().startsWith("23")) return -1;
            throw new IllegalStateException("Could not register the customer.", ex);
        }
    }

    public CustomerProfile findByUsername(String username) {
        String sql = "SELECT id,customer_type,first_name,surname,company_name,email,address,username FROM customers WHERE username=?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return null;
                String fullName = "COMPANY".equals(result.getString("customer_type"))
                        ? result.getString("company_name")
                        : result.getString("first_name") + " " + result.getString("surname");
                return new CustomerProfile(result.getLong("id"), result.getString("customer_type"), fullName,
                        result.getString("email"), result.getString("address"), result.getString("username"));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not find the customer.", ex);
        }
    }

    public boolean updateContact(String username, String email, String address) {
        String sql = "UPDATE customers SET email=?, address=? WHERE username=?";
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, address);
            statement.setString(3, username);
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not update the customer.", ex);
        }
    }

    public boolean delete(String username) {
        String sql = "DELETE FROM customers WHERE username=?";
        try (Connection connection = DatabaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            return statement.executeUpdate() == 1;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not delete the customer.", ex);
        }
    }

    public long getCustomerId(String username) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM customers WHERE username=?")) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getLong(1) : -1;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not find the customer ID.", ex);
        }
    }
}
