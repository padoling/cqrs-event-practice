package com.example.cqrseventpractice.domain;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private Set<Contact> contacts = new HashSet<>();
    private Set<Address> addresses = new HashSet<>();

    public User(String userId, String firstName, String lastName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
