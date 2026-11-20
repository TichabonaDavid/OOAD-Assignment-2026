package com.bank.model;

public record AccountSummary(String accountNumber, AccountType type, String branch, double balance) {
    @Override public String toString() {
        return "%s  |  %s  |  BWP %,.2f".formatted(accountNumber, type.label(), balance);
    }
}
