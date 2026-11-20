package com.bank.model;

public class ChequeAccount extends Account {
    private final String employmentDetails;

    public ChequeAccount(String accountNumber, String branch, double openingBalance, String employmentDetails) {
        super(accountNumber, branch, openingBalance);
        if (employmentDetails == null || employmentDetails.isBlank()) {
            throw new IllegalArgumentException("Employment details are required for a cheque account.");
        }
        this.employmentDetails = employmentDetails.trim();
    }

    public String getEmploymentDetails() { return employmentDetails; }

    @Override public void withdraw(double amount) {
        validatePositiveAmount(amount);
        if (amount > balance) throw new IllegalArgumentException("Insufficient funds.");
        balance -= amount;
    }

    @Override public void payInterest() { /* Cheque accounts do not earn interest. */ }
}
