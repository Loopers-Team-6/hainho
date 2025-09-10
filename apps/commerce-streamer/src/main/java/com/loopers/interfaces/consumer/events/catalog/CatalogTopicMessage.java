package com.loopers.interfaces.consumer.events.catalog;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.loopers.interfaces.consumer.events.KafkaMessage;

public record CatalogTopicMessage(
        String eventId,
        String eventType,
        java.time.ZonedDateTime producedAt,
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "eventType"
        )
        CatalogTopicEvent payload
) implements KafkaMessage<CatalogTopicEvent> {
}
