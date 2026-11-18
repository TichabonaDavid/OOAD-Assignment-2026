package com.bank.model;

public class ChequeAccount extends Account {
    private String employmentDetails;
    public ChequeAccount(String acc, String br, double bal, String emp) { 
        super(acc, br, bal); this.employmentDetails = emp; 
    }
    public String getEmploymentDetails() { return employmentDetails; }
    @Override public void withdraw(double amount) { if (amount > 0 && amount <= balance) balance -= amount; }
    @Override public void payInterest() { System.out.println("Cheque Account: Skipping interest."); }
}