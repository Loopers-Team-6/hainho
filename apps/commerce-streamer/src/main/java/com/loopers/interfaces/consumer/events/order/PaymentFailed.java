package com.loopers.interfaces.consumer.events.order;

public record PaymentFailed(
        Long orderId,
        Long paymentId
) implements OrderTopicEvent {
}
