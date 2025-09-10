package com.loopers.infrastructure.order;

import com.loopers.confg.kafka.KafkaTopics;
import com.loopers.domain.order.OrderCancelled;
import com.loopers.domain.order.OrderCompleted;
import com.loopers.domain.order.OrderCreated;
import com.loopers.infrastructure.kafka.KafkaMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEventKafkaProducer {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produce(OrderCreated event) {
        KafkaMessage<OrderCreated> kafkaMessage = KafkaMessage.from(event);
        kafkaTemplate.send(KafkaTopics.ORDER, event.orderId().toString(), kafkaMessage);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produce(OrderCancelled event) {
        KafkaMessage<OrderCancelled> kafkaMessage = KafkaMessage.from(event);
        kafkaTemplate.send(KafkaTopics.ORDER, event.orderId().toString(), kafkaMessage);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produce(OrderCompleted event) {
        KafkaMessage<OrderCompleted> kafkaMessage = KafkaMessage.from(event);
        kafkaTemplate.send(KafkaTopics.ORDER, event.orderId().toString(), kafkaMessage);
    }
}
