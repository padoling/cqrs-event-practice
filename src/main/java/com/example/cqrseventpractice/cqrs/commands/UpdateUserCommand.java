package com.example.cqrseventpractice.cqrs.commands;

import com.example.cqrseventpractice.domain.Address;
import com.example.cqrseventpractice.domain.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UpdateUserCommand {
    private String userId;
    private Set<Address> addresses;
    private Set<Contact> contacts;
}
