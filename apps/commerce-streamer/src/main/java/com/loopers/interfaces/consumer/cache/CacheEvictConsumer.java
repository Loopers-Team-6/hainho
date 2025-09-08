package com.loopers.interfaces.consumer.cache;

import com.loopers.application.cache.CacheEvictFacade;
import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.interfaces.consumer.KafkaMessage;
import com.loopers.interfaces.consumer.metrics.LikeProductCreated;
import com.loopers.interfaces.consumer.metrics.LikeProductDeleted;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CacheEvictConsumer {
    private final CacheEvictFacade cacheEvictFacade;

    @Value("${kafka.group.cache-evict}")
    private String groupId;

    @KafkaListener(
            topics = {"${kafka.topic.catalog}"},
            groupId = "${kafka.group.cache-evict}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consume(List<KafkaMessage<?>> messages, Acknowledgment acknowledgment) {
        for (KafkaMessage<?> message : messages) {
            switch (message.eventType()) {
                case "LikeProductCreated" -> {
                    LikeProductCreated event = (LikeProductCreated) message.payload();
                    cacheEvictFacade.evictProductCache(event, message.eventId(), groupId, message.producedAt());
                }
                case "LikeProductDeleted" -> {
                    LikeProductDeleted event = (LikeProductDeleted) message.payload();
                    cacheEvictFacade.evictProductCache(event, message.eventId(), groupId, message.producedAt());
                }
                default -> {
                }
            }
        }
        acknowledgment.acknowledge();
    }
}
