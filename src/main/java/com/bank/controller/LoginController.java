package com.bank.controller;

import com.bank.dao.CustomerDAO;
import com.bank.model.CustomerProfile;
import com.bank.model.Company;
import com.bank.model.Individual;

public class LoginController {
    private final CustomerDAO customerDAO;

    public LoginController() { this(new CustomerDAO()); }
    public LoginController(CustomerDAO customerDAO) { this.customerDAO = customerDAO; }

    public boolean handleLogin(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) return false;
        return customerDAO.authenticate(username.trim(), password);
    }

    public boolean registerIndividual(String firstName, String surname, String email, String address,
                                      String username, String password) {
        if (password == null || password.length() < 8) throw new IllegalArgumentException("Password must be at least 8 characters.");
        validateEmail(email);
        Individual customer = new Individual(0, email, address, username, password, firstName, surname);
        return customerDAO.create(customer) > 0;
    }

    public boolean registerCompany(String companyName, String registrationNumber, String email, String address,
                                   String username, String password) {
        if (password == null || password.length() < 8) throw new IllegalArgumentException("Password must be at least 8 characters.");
        validateEmail(email);
        Company customer = new Company(0, email, address, username, password, companyName, registrationNumber);
        return customerDAO.create(customer) > 0;
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
    }

    public CustomerProfile findCustomer(String username) { return customerDAO.findByUsername(username); }
}
