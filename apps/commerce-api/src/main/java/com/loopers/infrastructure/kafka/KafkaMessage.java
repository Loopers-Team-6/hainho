package com.loopers.infrastructure.kafka;

import java.time.ZonedDateTime;
import java.util.UUID;

public record KafkaMessage<T>(
        String eventId,
        String eventType,
        ZonedDateTime producedAt,
        T payload
) {
    public static <T> KafkaMessage<T> from(T payload) {
        return new KafkaMessage<>(
                UUID.randomUUID().toString(),
                payload.getClass().getSimpleName(),
                ZonedDateTime.now(),
                payload
        );
    }
}
