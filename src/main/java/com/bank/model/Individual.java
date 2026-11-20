package com.bank.model;

public class Individual extends Customer {
    private final String firstname;
    private final String surname;

    public Individual(int id, String email, String address, String username, String password, String firstname, String surname) {
        super(id, email, address, username, password);
        if (firstname == null || firstname.isBlank() || surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("First name and surname are required.");
        }
        this.firstname = firstname.trim();
        this.surname = surname.trim();
    }

    public String getFirstname() { return firstname; }
    public String getSurname() { return surname; }
    @Override public String getFullName() { return firstname + " " + surname; }
}
