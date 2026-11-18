package com.bank.model;
import java.util.ArrayList;
import java.util.List;

public class Bank {
    private String bankName, branch;
    private List<Account> accounts;
    public Bank(String bankName, String branch) {
        this.bankName = bankName; this.branch = branch; this.accounts = new ArrayList<>();
    }
    public String getBankName() { return bankName; }
    public String getBranch() { return branch; }
    public void openAccount(Account account) { accounts.add(account); }
    public List<Account> getAccounts() { return accounts; }
}