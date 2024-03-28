package com.example.cqrseventpractice.cqrs.projections;

import com.example.cqrseventpractice.cqrs.queries.AddressByRegionQuery;
import com.example.cqrseventpractice.cqrs.queries.ContactByTypeQuery;
import com.example.cqrseventpractice.cqrs.repository.UserReadRepository;
import com.example.cqrseventpractice.domain.Address;
import com.example.cqrseventpractice.domain.Contact;
import com.example.cqrseventpractice.domain.UserAddress;
import com.example.cqrseventpractice.domain.UserContact;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class UserProjection {
    private final UserReadRepository repository;

    public Set<Contact> handle(ContactByTypeQuery query) throws Exception {
        UserContact userContact = repository.getUserContact(query.getUserId());
        if (userContact == null) {
            throw new Exception("User does not exist.");
        }
        return userContact.getContactByType()
                .get(query.getContactType());
    }

    public Set<Address> handle(AddressByRegionQuery query) throws Exception {
        UserAddress userAddress = repository.getUserAddress(query.getUserId());
        if (userAddress == null) {
            throw new Exception("User does not exist.");
        }
        return userAddress.getAddressByRegion()
                .get(query.getState());
    }
}
