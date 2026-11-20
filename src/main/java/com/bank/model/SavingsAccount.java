package com.bank.model;

<<<<<<< HEAD
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SavingsAccount extends Account implements Interest {
    public static final double MONTHLY_RATE = 0.0005;

    public SavingsAccount(String accountNumber, String branch, double openingBalance) {
        super(accountNumber, branch, openingBalance);
    }

    @Override public double calculateInterest() {
        return BigDecimal.valueOf(balance * MONTHLY_RATE).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override public void creditInterest() { balance += calculateInterest(); }
    @Override public void payInterest() { creditInterest(); }
    @Override public void withdraw(double amount) {
        throw new UnsupportedOperationException("Withdrawals are not allowed from a savings account.");
    }
}
=======
public class SavingsAccount extends Account implements Interest {
    private double monthlyRate = 0.0005; // 0.05%
    public SavingsAccount(String acc, String br, double bal) { super(acc, br, bal); }

    @Override public double calculateInterest() { return balance * monthlyRate; }
    @Override public void creditInterest() { balance += calculateInterest(); }
    @Override public void payInterest() { creditInterest(); }
    @Override public void withdraw(double amount) { System.out.println("ERROR: Withdrawals not allowed."); }
}
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
