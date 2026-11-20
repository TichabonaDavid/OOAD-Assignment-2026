package com.bank.model;

public class ChequeAccount extends Account {
<<<<<<< HEAD
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
=======
    private String employmentDetails;
    public ChequeAccount(String acc, String br, double bal, String emp) { 
        super(acc, br, bal); this.employmentDetails = emp; 
    }
    public String getEmploymentDetails() { return employmentDetails; }
    @Override public void withdraw(double amount) { if (amount > 0 && amount <= balance) balance -= amount; }
    @Override public void payInterest() { System.out.println("Cheque Account: Skipping interest."); }
}
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
