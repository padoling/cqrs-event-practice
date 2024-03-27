package com.example.cqrseventpractice.domain;

import lombok.Data;

@Data
public class Contact {
    private String type;
    private String detail;

    public Contact(String type, String detail) {
        this.type = type;
        this.detail = detail;
    }
}
