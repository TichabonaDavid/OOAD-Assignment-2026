package com.bank.model;

public class Company extends Customer {
    private final String companyName;
    private final String companyRegNo;

    public Company(int id, String email, String address, String username, String password, String companyName, String companyRegNo) {
        super(id, email, address, username, password);
        if (companyName == null || companyName.isBlank() || companyRegNo == null || companyRegNo.isBlank()) {
            throw new IllegalArgumentException("Company name and registration number are required.");
        }
        this.companyName = companyName.trim();
        this.companyRegNo = companyRegNo.trim();
    }

    public String getCompanyName() { return companyName; }
    public String getCompanyRegNo() { return companyRegNo; }
    @Override public String getFullName() { return companyName; }
}
