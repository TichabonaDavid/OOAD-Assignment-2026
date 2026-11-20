package com.bank.model;

import java.time.LocalDateTime;

public record TransactionRecord(long id, String accountNumber, String type, double amount,
                               double balanceAfter, LocalDateTime createdAt, String description) { }
