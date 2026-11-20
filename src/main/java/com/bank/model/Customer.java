package com.bank.model;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Customer {
    private final int customerID;
    private final String email;
    private final String address;
    private final String username;
    private final String password;
    private final List<Account> accounts = new ArrayList<>();

    protected Customer(int customerID, String email, String address, String username, String password) {
        if (customerID < 0) throw new IllegalArgumentException("Customer ID cannot be negative.");
        this.customerID = customerID;
        this.email = requireText(email, "Email");
        this.address = requireText(address, "Address");
        this.username = requireText(username, "Username");
        this.password = requireText(password, "Password");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required.");
        return value.trim();
    }

=======
public abstract class Customer {
    private int customerID;
    private String email, address, username, password;

    public Customer(int customerID, String email, String address, String username, String password) {
        this.customerID = customerID; this.email = email; this.address = address;
        this.username = username; this.password = password;
    }
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
    public int getCustomerID() { return customerID; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
<<<<<<< HEAD
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
    public List<Account> getAccounts() { return Collections.unmodifiableList(accounts); }
    public double getTotalBalance() { return accounts.stream().mapToDouble(Account::getBalance).sum(); }

    public void addAccount(Account account) {
        if (account == null) throw new IllegalArgumentException("Account is required.");
        if (account instanceof ChequeAccount && !(this instanceof Individual)) {
            throw new IllegalArgumentException("Cheque accounts can only belong to working individuals.");
        }
        if (accounts.stream().anyMatch(existing -> existing.getAccountNumber().equals(account.getAccountNumber()))) {
            throw new IllegalArgumentException("This customer already has that account number.");
        }
        account.assignOwner(this);
        accounts.add(account);
    }

    public void openAccount(Account account) { addAccount(account); }
    public abstract String getFullName();
}
=======
    public abstract String getFullName();
}
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
