package com.loopers.interfaces.consumer.events.order;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderCreated.class, name = "OrderCreated"),
        @JsonSubTypes.Type(value = OrderCompleted.class, name = "OrderCompleted"),
        @JsonSubTypes.Type(value = OrderCancelled.class, name = "OrderCancelled"),
        @JsonSubTypes.Type(value = PaymentSucceed.class, name = "PaymentSucceed"),
        @JsonSubTypes.Type(value = PaymentFailed.class, name = "PaymentFailed"),
        @JsonSubTypes.Type(value = CardPaymentCreated.class, name = "CardPaymentCreated"),
        @JsonSubTypes.Type(value = PointPaymentCreated.class, name = "PointPaymentCreated")
})
public sealed interface OrderTopicEvent
        permits OrderCreated, OrderCompleted, OrderCancelled,
        PaymentSucceed, PaymentFailed, CardPaymentCreated, PointPaymentCreated {
}