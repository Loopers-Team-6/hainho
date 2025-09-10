package com.loopers.infrastructure.like;

import com.loopers.confg.kafka.KafkaTopics;
import com.loopers.domain.like.LikeProductCreated;
import com.loopers.domain.like.LikeProductDeleted;
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
public class LikeEventKafkaProducer {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produce(LikeProductCreated event) {
        KafkaMessage<LikeProductCreated> kafkaMessage = KafkaMessage.from(event);
        kafkaTemplate.send(KafkaTopics.CATALOG, event.productId().toString(), kafkaMessage);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produce(LikeProductDeleted event) {
        KafkaMessage<LikeProductDeleted> kafkaMessage = KafkaMessage.from(event);
        kafkaTemplate.send(KafkaTopics.CATALOG, event.productId().toString(), kafkaMessage);
    }
}