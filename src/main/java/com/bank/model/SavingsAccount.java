package com.bank.model;

public class SavingsAccount extends Account implements Interest {
    private double monthlyRate = 0.0005; // 0.05%
    public SavingsAccount(String acc, String br, double bal) { super(acc, br, bal); }

    @Override public double calculateInterest() { return balance * monthlyRate; }
    @Override public void creditInterest() { balance += calculateInterest(); }
    @Override public void payInterest() { creditInterest(); }
    @Override public void withdraw(double amount) { System.out.println("ERROR: Withdrawals not allowed."); }
}