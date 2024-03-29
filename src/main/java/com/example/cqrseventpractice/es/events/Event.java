package com.example.cqrseventpractice.es.events;

import lombok.ToString;

import java.util.Date;
import java.util.UUID;

// 모든 이벤트들이 상속받아야 하는 객체
@ToString
public abstract class Event {
    public final UUID id = UUID.randomUUID();
    public final Date created = new Date();
}
