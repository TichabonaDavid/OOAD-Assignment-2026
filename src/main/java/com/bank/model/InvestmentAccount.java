package com.bank.model;

public class InvestmentAccount extends Account implements Interest {
    private double monthlyRate = 0.05; // 5%
    private final double MIN_DEPOSIT = 500.00;
    public InvestmentAccount(String acc, String br, double bal) { super(acc, br, bal); }

    @Override public void deposit(double amount) {
        if (amount >= MIN_DEPOSIT) super.deposit(amount);
        else System.out.println("ERROR: Minimum deposit is BWP 500.00");
    }
    @Override public double calculateInterest() { return balance * monthlyRate; }
    @Override public void creditInterest() { balance += calculateInterest(); }
    @Override public void payInterest() { creditInterest(); }
    @Override public void withdraw(double amount) { if (amount > 0 && amount <= balance) balance -= amount; }
}