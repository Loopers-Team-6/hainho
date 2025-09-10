package com.loopers.interfaces.consumer.events.catalog;

public record LikeProductCreated(
        Long userId,
        Long productId
) implements CatalogTopicEvent {
}
