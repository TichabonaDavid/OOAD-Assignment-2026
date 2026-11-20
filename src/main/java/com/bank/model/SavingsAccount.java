package com.bank.model;

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
