package com.example.cqrseventpractice.cqrs.projectors;

import com.example.cqrseventpractice.cqrs.repository.UserReadRepository;
import com.example.cqrseventpractice.domain.*;

import java.util.*;

public class UserProjector {
    UserReadRepository readRepository = new UserReadRepository();

    public UserProjector(UserReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public void project(User user) {
        // readRepository에서 read side 도메인 모델을 꺼냄
        UserContact userContact = Optional.ofNullable(readRepository.getUserContact(user.getUserId()))
                .orElse(new UserContact());

        Map<String, Set<Contact>> contactByType = new HashMap<>();
        // User의 컬렉션 모델을 순회하며 read side 도메인 모델에 추가
        for (Contact contact : user.getContacts()) {
            // type별로 contact 모으기
            Set<Contact> contacts = Optional.ofNullable(contactByType.get(contact.getType()))
                    .orElse(new HashSet<>());
            contacts.add(contact);
            contactByType.put(contact.getType(), contacts);
        }

        // 값 세팅 후 readRepository에 add
        userContact.setContactByType(contactByType);
        readRepository.addUserContact(user.getUserId(), userContact);

        UserAddress userAddress = Optional.ofNullable(readRepository.getUserAddress(user.getUserId()))
                .orElse(new UserAddress());
        Map<String, Set<Address>> addressByRegion = new HashMap<>();
        for (Address address : user.getAddresses()) {
            Set<Address> addresses = Optional.ofNullable(addressByRegion.get(address.getState()))
                    .orElse(new HashSet<>());
            addresses.add(address);
            addressByRegion.put(address.getState(), addresses);
        }
        userAddress.setAddressByRegion(addressByRegion);
        readRepository.addUserAddress(user.getUserId(), userAddress);
    }
}
