package com.loopers.interfaces.consumer.events.order;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.loopers.interfaces.consumer.events.KafkaMessage;

import java.time.ZonedDateTime;

public record OrderTopicMessage(
        String eventId,
        String eventType,
        ZonedDateTime producedAt,
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "eventType"
        )
        OrderTopicEvent payload
) implements KafkaMessage<OrderTopicEvent> {
}
