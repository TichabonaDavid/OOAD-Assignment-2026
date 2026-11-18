package com.bank.model;

public class Individual extends Customer {
    private String firstname, surname;
    public Individual(int id, String email, String address, String uname, String pwd, String fname, String sname) {
        super(id, email, address, uname, pwd);
        this.firstname = fname; this.surname = sname;
    }
    @Override public String getFullName() { return firstname + " " + surname; }
}