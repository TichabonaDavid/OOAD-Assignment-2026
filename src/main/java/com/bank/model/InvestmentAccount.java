package com.bank.model;

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
