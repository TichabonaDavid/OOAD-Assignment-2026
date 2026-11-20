package com.bank.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank {
    private final String bankName;
    private final String branch;
    private final List<Customer> customers = new ArrayList<>();

    public Bank(String bankName, String branch) {
        if (bankName == null || bankName.isBlank() || branch == null || branch.isBlank()) {
            throw new IllegalArgumentException("Bank name and branch are required.");
        }
        this.bankName = bankName.trim();
        this.branch = branch.trim();
    }

    public String getBankName() { return bankName; }
    public String getBranch() { return branch; }

    public void registerCustomer(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("Customer is required.");
        if (customers.stream().anyMatch(existing -> existing.getCustomerID() == customer.getCustomerID())) {
            throw new IllegalArgumentException("Customer is already registered.");
        }
        customers.add(customer);
    }

    public void openAccount(Customer customer, Account account) {
        if (!customers.contains(customer)) throw new IllegalArgumentException("Register the customer before opening an account.");
        customer.openAccount(account);
    }

    public List<Customer> getCustomers() { return Collections.unmodifiableList(customers); }
    public List<Account> getAccounts() {
        return customers.stream().flatMap(customer -> customer.getAccounts().stream()).toList();
    }
}
