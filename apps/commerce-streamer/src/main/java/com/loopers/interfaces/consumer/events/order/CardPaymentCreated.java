package com.loopers.interfaces.consumer.events.order;

public record CardPaymentCreated(
        Long paymentId,
        Long orderId,
        String cardType,
        String cardNumber,
        Long amount
) implements OrderTopicEvent {
}
