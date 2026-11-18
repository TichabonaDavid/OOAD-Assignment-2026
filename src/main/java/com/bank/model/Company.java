package com.bank.model;

public class Company extends Customer {
    private String companyName, companyRegNo;
    public Company(int id, String email, String address, String uname, String pwd, String cName, String regNo) {
        super(id, email, address, uname, pwd);
        this.companyName = cName; this.companyRegNo = regNo;
    }
    public String getCompanyName() { return companyName; }
    public String getCompanyRegNo() { return companyRegNo; }
    @Override public String getFullName() { return companyName; }
}