package com.loopers.interfaces.consumer.events.order;

import java.util.List;

public record OrderCreated(
        Long orderId,
        Long userId,
        Long couponId,
        List<OrderItem> items,
        Long totalPrice
) implements OrderTopicEvent {
    public record OrderItem(
            Long productId,
            Long quantity
    ) {
    }
}
