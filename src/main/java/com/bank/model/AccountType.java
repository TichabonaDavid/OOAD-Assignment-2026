package com.bank.model;

public enum AccountType {
    SAVINGS("Savings", SavingsAccount.MONTHLY_RATE, false),
    INVESTMENT("Investment", InvestmentAccount.MONTHLY_RATE, true),
    CHEQUE("Cheque", 0.0, true);

    private final String label;
    private final double monthlyRate;
    private final boolean withdrawalsAllowed;

    AccountType(String label, double monthlyRate, boolean withdrawalsAllowed) {
        this.label = label;
        this.monthlyRate = monthlyRate;
        this.withdrawalsAllowed = withdrawalsAllowed;
    }

    public String label() { return label; }
    public double monthlyRate() { return monthlyRate; }
    public boolean withdrawalsAllowed() { return withdrawalsAllowed; }

    public static AccountType fromLabel(String value) {
        if (value == null) throw new IllegalArgumentException("Account type is required.");
        for (AccountType type : values()) {
            if (type.name().equalsIgnoreCase(value.trim()) || type.label.equalsIgnoreCase(value.trim())) return type;
        }
        throw new IllegalArgumentException("Unsupported account type: " + value);
    }
}
