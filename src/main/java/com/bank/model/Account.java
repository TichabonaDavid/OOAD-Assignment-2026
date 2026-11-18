package com.bank.model;

public abstract class Account {
    private String accountNumber, branch;
    protected double balance; 

    public Account(String accountNumber, String branch, double balance) {
        this.accountNumber = accountNumber; this.branch = branch; this.balance = balance;
    }
    public String getAccountNumber() { return accountNumber; }
    public String getBranch() { return branch; }
    public double getBalance() { return balance; }

    public void deposit(double amount) { if (amount > 0) balance += amount; }
    public void deposit(double amount, String description) { deposit(amount); }
    public abstract void withdraw(double amount);
    public abstract void payInterest();
}