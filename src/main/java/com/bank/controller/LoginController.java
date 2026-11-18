package com.bank.controller;
import com.bank.dao.CustomerDAO;

public class LoginController {
    private CustomerDAO customerDAO = new CustomerDAO();
    public boolean handleLogin(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) return false;
        return customerDAO.authenticate(username, password);
    }
}