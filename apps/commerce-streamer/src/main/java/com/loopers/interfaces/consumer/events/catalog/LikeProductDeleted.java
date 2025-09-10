package com.loopers.interfaces.consumer.events.catalog;

public record LikeProductDeleted(
        Long userId,
        Long productId
) implements CatalogTopicEvent {
}
