package com.loopers.interfaces.consumer.events.order;

public record PointPaymentCreated(
        Long userId,
        Long paymentId,
        Long orderId,
        Long amount
) implements OrderTopicEvent {
}
