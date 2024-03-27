package com.example.cqrseventpractice.domain;

import lombok.Data;

@Data
public class Address {
    private String city;
    private String state;
    private String postcode;

    public Address(String city, String state, String postcode) {
        this.city = city;
        this.state = state;
        this.postcode = postcode;
    }
}
