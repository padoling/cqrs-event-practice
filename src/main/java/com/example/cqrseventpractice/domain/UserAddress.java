package com.example.cqrseventpractice.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class UserAddress {
    // region별로 address를 모은 map
    private Map<String, Set<Address>> addressByRegion = new HashMap<>();
}
