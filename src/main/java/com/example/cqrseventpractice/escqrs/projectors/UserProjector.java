package com.example.cqrseventpractice.escqrs.projectors;

import com.example.cqrseventpractice.cqrs.repository.UserReadRepository;
import com.example.cqrseventpractice.domain.Address;
import com.example.cqrseventpractice.domain.Contact;
import com.example.cqrseventpractice.domain.UserAddress;
import com.example.cqrseventpractice.domain.UserContact;
import com.example.cqrseventpractice.es.events.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserProjector {

    UserReadRepository readRepository = new UserReadRepository();

    public UserProjector(UserReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public void project(String userId, List<Event> events) {
        for (Event event : events) {
            if (event instanceof UserAddressAddedEvent)
                apply(userId, (UserAddressAddedEvent) event);
            if (event instanceof UserAddressRemovedEvent)
                apply(userId, (UserAddressRemovedEvent) event);
            if (event instanceof UserContactAddedEvent)
                apply(userId, (UserContactAddedEvent) event);
            if (event instanceof UserContactRemovedEvent)
                apply(userId, (UserContactRemovedEvent) event);
        }
    }

    public void apply(String userId, UserAddressAddedEvent event) {
        Address address = new Address(event.getCity(), event.getState(), event.getPostCode());
        UserAddress userAddress = Optional.ofNullable(readRepository.getUserAddress(userId))
                .orElse(new UserAddress());
        Set<Address> addresses = Optional.ofNullable(userAddress.getAddressByRegion().get(address.getState()))
                .orElse(new HashSet<>());
        addresses.add(address);
        userAddress.getAddressByRegion().put(address.getState(), addresses);
        readRepository.addUserAddress(userId, userAddress);
    }

    public void apply(String userId, UserAddressRemovedEvent event) {
        Address address = new Address(event.getCity(), event.getState(), event.getPostCode());
        UserAddress userAddress = readRepository.getUserAddress(userId);
        if (userAddress != null) {
            Set<Address> addresses = userAddress.getAddressByRegion()
                    .get(address.getState());
            if (addresses != null)
                addresses.remove(address);
            readRepository.addUserAddress(userId, userAddress);
        }
    }

    public void apply(String userId, UserContactAddedEvent event) {
        Contact contact = new Contact(event.getContactType(), event.getContactDetails());
        UserContact userContact = Optional.ofNullable(readRepository.getUserContact(userId))
                .orElse(new UserContact());
        Set<Contact> contacts = Optional.ofNullable(userContact.getContactByType()
                        .get(contact.getType()))
                .orElse(new HashSet<>());
        contacts.add(contact);
        userContact.getContactByType()
                .put(contact.getType(), contacts);
        readRepository.addUserContact(userId, userContact);
    }

    public void apply(String userId, UserContactRemovedEvent event) {
        Contact contact = new Contact(event.getContactType(), event.getContactDetails());
        UserContact userContact = readRepository.getUserContact(userId);
        if (userContact != null) {
            Set<Contact> contacts = userContact.getContactByType()
                    .get(contact.getType());
            if (contacts != null)
                contacts.remove(contact);
            readRepository.addUserContact(userId, userContact);
        }
    }
}
