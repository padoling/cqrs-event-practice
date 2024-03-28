package com.example.cqrseventpractice.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class UserContact {
    // type별로 contact를 모은 map
    private Map<String, Set<Contact>> contactByType = new HashMap<>();
}
