package com.example.cqrseventpractice.es;

import com.example.cqrseventpractice.domain.Address;
import com.example.cqrseventpractice.domain.Contact;
import com.example.cqrseventpractice.es.repository.EventStore;
import com.example.cqrseventpractice.es.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationUnitTest {

    private EventStore repository;
    private UserService service;

    @BeforeEach
    public void setUp() {
        repository = new EventStore();
        service = new UserService(repository);
    }

    @Test
    public void givenCRUDApplication_whenDataCreated_thenDataCanBeFetched() throws Exception {
        String userId = UUID.randomUUID().toString();

        service.createUser(userId, "Tom", "Sawyer");
        service.updateUser(userId, Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"), new Contact("EMAIL", "tom.sawyer@rediff.com"), new Contact("PHONE", "700-000-0001"))
                        .collect(Collectors.toSet()),
                Stream.of(new Address("New York", "NY", "10001"), new Address("Los Angeles", "CA", "90001"), new Address("Housten", "TX", "77001"))
                        .collect(Collectors.toSet()));
        service.updateUser(userId, Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"), new Contact("PHONE", "700-000-0001"))
                        .collect(Collectors.toSet()),
                Stream.of(new Address("New York", "NY", "10001"), new Address("Housten", "TX", "77001"))
                        .collect(Collectors.toSet()));

        assertEquals(Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"))
                .collect(Collectors.toSet()), service.getContactByType(userId, "EMAIL"));
        assertEquals(Stream.of(new Address("New York", "NY", "10001"))
                .collect(Collectors.toSet()), service.getAddressByRegion(userId, "NY"));
    }
}
