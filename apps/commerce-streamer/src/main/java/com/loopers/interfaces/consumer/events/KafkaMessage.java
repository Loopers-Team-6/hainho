package com.loopers.interfaces.consumer.events;

import java.time.ZonedDateTime;

public interface KafkaMessage<P> {
    String eventId();

    String eventType();

    ZonedDateTime producedAt();

    P payload();
}
