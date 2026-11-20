package com.bank.controller;

import com.bank.dao.AccountDAO;
import com.bank.model.AccountSummary;
import com.bank.model.AccountType;
import com.bank.model.TransactionRecord;

import java.time.YearMonth;
import java.util.List;

public class AccountController {
    private final AccountDAO accountDAO;

    public AccountController() { this(new AccountDAO()); }
    public AccountController(AccountDAO accountDAO) { this.accountDAO = accountDAO; }

    public List<AccountSummary> getAccounts(String username) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Customer session is required.");
        return accountDAO.listForCustomer(username);
    }

    public AccountSummary openAccount(String username, String accountType, String branch,
                                      double openingBalance, String employmentDetails) {
        return accountDAO.createAccount(username, AccountType.fromLabel(accountType), branch, openingBalance, employmentDetails);
    }

    public double getBalance(String username, String accountNumber) {
        return ownsAccount(username, accountNumber) ? accountDAO.getBalance(accountNumber) : -1;
    }

    public double deposit(String username, String accountNumber, double amount) {
        return ownsAccount(username, accountNumber) ? accountDAO.deposit(accountNumber, amount) : -1;
    }

    public double withdraw(String username, String accountNumber, double amount) {
        return ownsAccount(username, accountNumber) ? accountDAO.withdraw(accountNumber, amount) : -1;
    }

    public List<TransactionRecord> getTransactionHistory(String username, String accountNumber) {
        if (!ownsAccount(username, accountNumber)) return List.of();
        return accountDAO.transactionHistory(accountNumber);
    }

    public int payMonthlyInterest(YearMonth month) { return accountDAO.payMonthlyInterest(month); }

    private boolean ownsAccount(String username, String accountNumber) {
        if (username == null || accountNumber == null) return false;
        return accountDAO.listForCustomer(username).stream()
                .anyMatch(account -> account.accountNumber().equals(accountNumber));
    }
}