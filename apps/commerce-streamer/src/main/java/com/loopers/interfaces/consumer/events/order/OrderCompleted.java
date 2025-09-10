package com.loopers.interfaces.consumer.events.order;

import java.util.List;

public record OrderCompleted(
        Long orderId,
        Long userId,
        Long totalPrice,
        List<OrderItem> items
) implements OrderTopicEvent {
    public record OrderItem(
            Long productId,
            Long quantity,
            Long price
    ) {
    }
}
