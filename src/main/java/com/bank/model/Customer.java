package com.bank.model;

public abstract class Customer {
    private int customerID;
    private String email, address, username, password;

    public Customer(int customerID, String email, String address, String username, String password) {
        this.customerID = customerID; this.email = email; this.address = address;
        this.username = username; this.password = password;
    }
    public int getCustomerID() { return customerID; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public abstract String getFullName();
}