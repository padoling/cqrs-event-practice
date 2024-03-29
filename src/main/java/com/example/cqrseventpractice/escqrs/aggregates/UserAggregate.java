package com.example.cqrseventpractice.escqrs.aggregates;

import com.example.cqrseventpractice.cqrs.commands.CreateUserCommand;
import com.example.cqrseventpractice.cqrs.commands.UpdateUserCommand;
import com.example.cqrseventpractice.domain.Address;
import com.example.cqrseventpractice.domain.Contact;
import com.example.cqrseventpractice.domain.User;
import com.example.cqrseventpractice.es.events.*;
import com.example.cqrseventpractice.es.repository.EventStore;
import com.example.cqrseventpractice.es.service.UserUtility;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserAggregate {
    private final EventStore writeRepository;

    public List<Event> handleCreateUserCommand(CreateUserCommand command) {
        UserCreatedEvent event = new UserCreatedEvent(command.getUserId(), command.getFirstName(), command.getLastName());
        writeRepository.addEvent(command.getUserId(), event);
        return List.of(event);
    }

    public List<Event> handleUpdateUserCommand(UpdateUserCommand command) {
        User user = UserUtility.recreateUserState(writeRepository, command.getUserId());
        List<Event> events = new ArrayList<>();

        List<Contact> contactsToRemove = user.getContacts().stream()
                .filter(c -> !command.getContacts().contains(c))
                .toList();
        for (Contact contact : contactsToRemove) {
            UserContactRemovedEvent contactRemovedEvent = new UserContactRemovedEvent(contact.getType(), contact.getDetail());
            events.add(contactRemovedEvent);
            writeRepository.addEvent(command.getUserId(), contactRemovedEvent);
        }
        List<Contact> contactsToAdd = command.getContacts()
                .stream()
                .filter(c -> !user.getContacts()
                        .contains(c))
                .toList();
        for (Contact contact : contactsToAdd) {
            UserContactAddedEvent contactAddedEvent = new UserContactAddedEvent(contact.getType(), contact.getDetail());
            events.add(contactAddedEvent);
            writeRepository.addEvent(command.getUserId(), contactAddedEvent);
        }

        List<Address> addressesToRemove = user.getAddresses()
                .stream()
                .filter(a -> !command.getAddresses()
                        .contains(a))
                .toList();
        for (Address address : addressesToRemove) {
            UserAddressRemovedEvent addressRemovedEvent = new UserAddressRemovedEvent(address.getCity(), address.getState(), address.getPostcode());
            events.add(addressRemovedEvent);
            writeRepository.addEvent(command.getUserId(), addressRemovedEvent);
        }

        List<Address> addressesToAdd = command.getAddresses()
                .stream()
                .filter(a -> !user.getAddresses()
                        .contains(a))
                .toList();
        for (Address address : addressesToAdd) {
            UserAddressAddedEvent addressAddedEvent = new UserAddressAddedEvent(address.getCity(), address.getState(), address.getPostcode());
            events.add(addressAddedEvent);
            writeRepository.addEvent(command.getUserId(), addressAddedEvent);
        }

        return events;
    }
}
