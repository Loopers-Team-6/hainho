package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductFound;
import com.loopers.infrastructure.kafka.KafkaMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEventKafkaProducer {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${kafka.topics.catalog}")
    private String topic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produce(ProductFound event) {
        KafkaMessage<ProductFound> kafkaMessage = KafkaMessage.from(event);
        // 메트릭용
        // 순서 보장이 필요 없으므로 key 없이 전송
        kafkaTemplate.send(topic, kafkaMessage);
    }
}
