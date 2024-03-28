package com.example.cqrseventpractice.cqrs;

import com.example.cqrseventpractice.cqrs.aggregates.UserAggregate;
import com.example.cqrseventpractice.cqrs.commands.CreateUserCommand;
import com.example.cqrseventpractice.cqrs.commands.UpdateUserCommand;
import com.example.cqrseventpractice.cqrs.projections.UserProjection;
import com.example.cqrseventpractice.cqrs.projectors.UserProjector;
import com.example.cqrseventpractice.cqrs.queries.AddressByRegionQuery;
import com.example.cqrseventpractice.cqrs.queries.ContactByTypeQuery;
import com.example.cqrseventpractice.cqrs.repository.UserReadRepository;
import com.example.cqrseventpractice.cqrs.repository.UserWriteRepository;
import com.example.cqrseventpractice.domain.Address;
import com.example.cqrseventpractice.domain.Contact;
import com.example.cqrseventpractice.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationUnitTest {

    private UserWriteRepository writeRepository;
    private UserReadRepository readRepository;
    private UserProjector projector;
    private UserAggregate userAggregate;
    private UserProjection userProjection;

    @BeforeEach
    public void setUp() {
        writeRepository = new UserWriteRepository();
        readRepository = new UserReadRepository();
        projector = new UserProjector(readRepository);
        userAggregate = new UserAggregate(writeRepository);
        userProjection = new UserProjection(readRepository);
    }

    @Test
    public void givenCQRSApplication_whenCommandRun_thenQueryShouldReturnResult() throws Exception {
        String userId = UUID.randomUUID().toString();
        User user = null;
        // user 생성 - command 객체를 이용해 aggregate에서 객체 생성 후 저장
        CreateUserCommand createUserCommand = new CreateUserCommand(userId, "Tom", "Sawyer");
        user = userAggregate.handleCreateUserCommand(createUserCommand);
        // read repository와 sync
        projector.project(user);

        UpdateUserCommand updateUserCommand = new UpdateUserCommand(user.getUserId(), Stream.of(new Address("New York", "NY", "10001"), new Address("Los Angeles", "CA", "90001"))
                .collect(Collectors.toSet()),
                Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"), new Contact("EMAIL", "tom.sawyer@rediff.com"))
                        .collect(Collectors.toSet()));
        user = userAggregate.handleUpdateUserCommand(updateUserCommand);
        projector.project(user);

        updateUserCommand = new UpdateUserCommand(userId, Stream.of(new Address("New York", "NY", "10001"), new Address("Housten", "TX", "77001"))
                .collect(Collectors.toSet()),
                Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"), new Contact("PHONE", "700-000-0001"))
                        .collect(Collectors.toSet()));
        user = userAggregate.handleUpdateUserCommand(updateUserCommand);
        projector.project(user);

        // 쿼리 객체
        ContactByTypeQuery contactByTypeQuery = new ContactByTypeQuery(userId, "EMAIL");
        // projection을 통해 쿼리한 객체가 위에서 마지막으로 변경한 값과 같은지 체크
        assertEquals(Stream.of(new Contact("EMAIL", "tom.sawyer@gmail.com"))
                .collect(Collectors.toSet()), userProjection.handle(contactByTypeQuery));
        AddressByRegionQuery addressByRegionQuery = new AddressByRegionQuery(userId, "NY");
        assertEquals(Stream.of(new Address("New York", "NY", "10001"))
                .collect(Collectors.toSet()), userProjection.handle(addressByRegionQuery));
    }
}
