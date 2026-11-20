package com.bank.model;

<<<<<<< HEAD
import java.math.BigDecimal;
import java.math.RoundingMode;

public class InvestmentAccount extends Account implements Interest {
    public static final double MONTHLY_RATE = 0.05;
    public static final double MINIMUM_OPENING_DEPOSIT = 500.00;

    public InvestmentAccount(String accountNumber, String branch, double openingBalance) {
        super(accountNumber, branch, openingBalance);
        if (openingBalance < MINIMUM_OPENING_DEPOSIT) {
            throw new IllegalArgumentException("An investment account requires an opening deposit of at least BWP 500.00.");
        }
    }

    @Override public double calculateInterest() {
        return BigDecimal.valueOf(balance * MONTHLY_RATE).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override public void creditInterest() { balance += calculateInterest(); }
    @Override public void payInterest() { creditInterest(); }
    @Override public void withdraw(double amount) {
        validatePositiveAmount(amount);
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds.");
        balance -= amount;
    }
}
=======
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
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
