package com.loopers.interfaces.consumer.cache;

import com.loopers.application.cache.CacheEvictFacade;
import com.loopers.confg.kafka.KafkaTopics;
import com.loopers.interfaces.consumer.KafkaMessage;
import com.loopers.interfaces.consumer.metrics.LikeProductCreated;
import com.loopers.interfaces.consumer.metrics.LikeProductDeleted;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CacheEvictConsumer {
    private final CacheEvictFacade cacheEvictFacade;

    @KafkaListener(
            topics = {KafkaTopics.CATALOG},
            groupId = CacheEvictKafkaConfig.CACHE_EVICT_GROUP,
            containerFactory = CacheEvictKafkaConfig.CACHE_EVICT_LISTENER
    )
    public void consume(List<KafkaMessage<?>> messages, Acknowledgment acknowledgment) {
        for (KafkaMessage<?> message : messages) {
            switch (message.eventType()) {
                case "LikeProductCreated" -> {
                    LikeProductCreated event = (LikeProductCreated) message.payload();
                    cacheEvictFacade.evictProductCache(event, message.eventId(), CacheEvictKafkaConfig.CACHE_EVICT_GROUP, message.producedAt());
                }
                case "LikeProductDeleted" -> {
                    LikeProductDeleted event = (LikeProductDeleted) message.payload();
                    cacheEvictFacade.evictProductCache(event, message.eventId(), CacheEvictKafkaConfig.CACHE_EVICT_GROUP, message.producedAt());
                }
                default -> {
                }
            }
        }
        acknowledgment.acknowledge();
    }
}
