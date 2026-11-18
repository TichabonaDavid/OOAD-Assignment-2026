package com.bank.controller;
import com.bank.dao.AccountDAO;

public class AccountController {
    private AccountDAO accountDAO = new AccountDAO();
    public double handleDeposit(String accountNumber, double amount) {
        if (amount <= 0) return -1; 
        double currentBalance = accountDAO.getBalance(accountNumber);
        if (currentBalance < 0) return -1; 
        double newBalance = currentBalance + amount;
        accountDAO.updateBalance(accountNumber, newBalance);
        accountDAO.recordTransaction(accountNumber, "DEPOSIT", amount);
        return newBalance;
    }
}