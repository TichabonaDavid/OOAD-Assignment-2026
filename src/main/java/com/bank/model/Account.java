package com.bank.model;

public abstract class Account {
    private final String accountNumber;
    private final String branch;
    private Customer owner;
    protected double balance;

    protected Account(String accountNumber, String branch, double openingBalance) {
        if (accountNumber == null || accountNumber.isBlank()) throw new IllegalArgumentException("Account number is required.");
        if (branch == null || branch.isBlank()) throw new IllegalArgumentException("Branch is required.");
        if (!Double.isFinite(openingBalance) || openingBalance < 0) throw new IllegalArgumentException("Opening balance cannot be negative.");
        this.accountNumber = accountNumber.trim();
        this.branch = branch.trim();
        this.balance = openingBalance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getBranch() { return branch; }
    public double getBalance() { return balance; }
    public Customer getOwner() { return owner; }

    void assignOwner(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("An account must belong to a customer.");
        if (owner != null && owner != customer) throw new IllegalStateException("An account already belongs to another customer.");
        owner = customer;
    }

    public void deposit(double amount) { deposit(amount, "Cash deposit"); }

    public void deposit(double amount, String description) {
        validatePositiveAmount(amount);
        balance += amount;
    }

    protected static void validatePositiveAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero.");
    }

    public abstract void withdraw(double amount);
    public abstract void payInterest();
}
