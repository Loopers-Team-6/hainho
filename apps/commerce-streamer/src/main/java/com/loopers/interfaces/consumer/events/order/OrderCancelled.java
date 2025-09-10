package com.loopers.interfaces.consumer.events.order;

import java.util.List;

public record OrderCancelled(
        Long orderId,
        List<OrderItem> items
) implements OrderTopicEvent {
    public record OrderItem(
            Long productId,
            Long quantity
    ) {
    }
}
