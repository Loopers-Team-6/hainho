package com.loopers.interfaces.consumer.events.order;

public record PaymentSucceed(
        Long orderId,
        Long paymentId
) implements OrderTopicEvent {
}
