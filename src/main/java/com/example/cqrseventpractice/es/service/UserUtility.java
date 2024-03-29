package com.example.cqrseventpractice.es.service;

import com.example.cqrseventpractice.domain.Address;
import com.example.cqrseventpractice.domain.Contact;
import com.example.cqrseventpractice.domain.User;
import com.example.cqrseventpractice.es.events.*;
import com.example.cqrseventpractice.es.repository.EventStore;

import java.util.List;

public class UserUtility {

    public static User recreateUserState(EventStore store, String userId) {
        User user = null;

        // userId에 해당하는 모든 이벤트를 순회하며 현재 상태의 User 객체를 만든다.
        List<Event> events = store.getEvents(userId);
        for (Event event : events) {
            if (event instanceof UserCreatedEvent) {
                UserCreatedEvent e = (UserCreatedEvent) event;
                user = new User(e.getUserId(), e.getFirstName(), e.getLastName());
            }
            if (event instanceof UserAddressAddedEvent) {
                UserAddressAddedEvent e = (UserAddressAddedEvent) event;
                Address address = new Address(e.getCity(), e.getState(), e.getPostCode());
                if (user != null) {
                    user.getAddresses().add(address);
                }
            }
            if (event instanceof UserAddressRemovedEvent) {
                UserAddressRemovedEvent e = (UserAddressRemovedEvent) event;
                Address address = new Address(e.getCity(), e.getState(), e.getPostCode());
                if (user != null) {
                    user.getAddresses().remove(address);
                }
            }
            if (event instanceof UserContactAddedEvent) {
                UserContactAddedEvent e = (UserContactAddedEvent) event;
                Contact contact = new Contact(e.getContactType(), e.getContactDetails());
                if (user != null)
                    user.getContacts()
                            .add(contact);
            }
            if (event instanceof UserContactRemovedEvent) {
                UserContactRemovedEvent e = (UserContactRemovedEvent) event;
                Contact contact = new Contact(e.getContactType(), e.getContactDetails());
                if (user != null)
                    user.getContacts()
                            .remove(contact);
            }
        }

        return user;
    }
}
