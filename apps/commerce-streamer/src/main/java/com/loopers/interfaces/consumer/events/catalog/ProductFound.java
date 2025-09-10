package com.loopers.interfaces.consumer.events.catalog;

public record ProductFound(
        Long productId,
        Long userId
) implements CatalogTopicEvent {
}
