package com.bank.model;

public record CustomerProfile(long id, String customerType, String fullName, String email,
                              String address, String username) { }
