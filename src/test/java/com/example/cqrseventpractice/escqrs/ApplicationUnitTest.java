package com.example.cqrseventpractice.escqrs;

import com.example.cqrseventpractice.cqrs.commands.CreateUserCommand;
import com.example.cqrseventpractice.cqrs.commands.UpdateUserCommand;
import com.example.cqrseventpractice.cqrs.projections.UserProjection;
import com.example.cqrseventpractice.cqrs.queries.AddressByRegionQuery;
import com.example.cqrseventpractice.cqrs.queries.ContactByTypeQuery;
import com.example.cqrseventpractice.cqrs.repository.UserReadRepository;
import com.example.cqrseventpractice.domain.Address;
import com.example.cqrseventpractice.domain.Contact;
import com.example.cqrseventpractice.es.events.Event;
import com.example.cqrseventpractice.es.repository.EventStore;
import com.example.cqrseventpractice.escqrs.aggregates.UserAggregate;
import com.example.cqrseventpractice.escqrs.projectors.UserProjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationUnitTest {
    private EventStore writeRepository;
    private UserReadRepository readRepository;
    private UserProjector projector;
    private UserAggregate userAggregate;
    private UserProjection userProjection;

    @BeforeEach
    public void setUp() {
        writeRepository = new EventStore();
        readRepository = new UserReadRepository();
        projector = new UserProjector(readRepository);
        userAggregate = new UserAggregate(writeRepository);
        userProjection = new UserProjection(readRepository);
    }

    @Test
    public void givenCQRSApplication_whenCommandRun_thenQueryShouldReturnResult() throws Exception {
        String userId = UUID.randomUUID().toString();
        List<Event> events = null;
        // command를 통해 이벤트 생성
        CreateUserCommand createUserCommand = new CreateUserCommand(userId, "Kumar", "Chandrakant");
        events = userAggregate.handleCreateUserCommand(createUserCommand);
        // project로 write side와 read side sync 맞추기
        projector.project(userId, events);

        UpdateUserCommand updateUserCommand = new UpdateUserCommand(userId, Stream.of(new Address("New York", "NY", "10001"), new Address("Los Angeles", "CA", "90001"))
                .collect(Collectors.toSet()),
                Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"), new Contact("EMAIL", "tom.sawyer@rediff.com"))
                        .collect(Collectors.toSet()));
        events = userAggregate.handleUpdateUserCommand(updateUserCommand);
        projector.project(userId, events);

        updateUserCommand = new UpdateUserCommand(userId, Stream.of(new Address("New York", "NY", "10001"), new Address("Housten", "TX", "77001"))
                .collect(Collectors.toSet()),
                Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"), new Contact("PHONE", "700-000-0001"))
                        .collect(Collectors.toSet()));
        events = userAggregate.handleUpdateUserCommand(updateUserCommand);
        projector.project(userId, events);

        // query로 조회한 데이터가 위에서 넣은 데이터와 같은지 확인
        ContactByTypeQuery contactByTypeQuery = new ContactByTypeQuery(userId, "EMAIL");
        assertEquals(Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"))
                .collect(Collectors.toSet()), userProjection.handle(contactByTypeQuery));
        AddressByRegionQuery addressByRegionQuery = new AddressByRegionQuery(userId, "NY");
        assertEquals(Stream.of(new Address("New York", "NY", "10001"))
                .collect(Collectors.toSet()), userProjection.handle(addressByRegionQuery));
    }
}
