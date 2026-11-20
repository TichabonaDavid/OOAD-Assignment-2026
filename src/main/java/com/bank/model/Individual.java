package com.bank.model;

public class Individual extends Customer {
<<<<<<< HEAD
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
=======
    private String firstname, surname;
    public Individual(int id, String email, String address, String uname, String pwd, String fname, String sname) {
        super(id, email, address, uname, pwd);
        this.firstname = fname; this.surname = sname;
    }
    @Override public String getFullName() { return firstname + " " + surname; }
}
>>>>>>> 220fe29f91bfccbcde2cde4d80788c205cb129b5
