package com.example.cqrseventpractice.cqrs.aggregates;

import com.example.cqrseventpractice.cqrs.commands.CreateUserCommand;
import com.example.cqrseventpractice.cqrs.commands.UpdateUserCommand;
import com.example.cqrseventpractice.cqrs.repository.UserWriteRepository;
import com.example.cqrseventpractice.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserAggregate {
    private final UserWriteRepository writeRepository;

    public User handleCreateUserCommand(CreateUserCommand command) {
        User user = new User(command.getUserId(), command.getFirstName(), command.getLastName());
        writeRepository.addUser(user.getUserId(), user);
        return user;
    }

    public User handleUpdateUserCommand(UpdateUserCommand command) {
        User user = writeRepository.getUser(command.getUserId());
        user.setAddresses(command.getAddresses());
        user.setContacts(command.getContacts());
        writeRepository.addUser(user.getUserId(), user);
        return user;
    }
}
