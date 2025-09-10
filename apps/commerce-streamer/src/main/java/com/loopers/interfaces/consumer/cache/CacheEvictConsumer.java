package com.loopers.interfaces.consumer.cache;

import com.loopers.application.cache.CacheEvictFacade;
import com.loopers.confg.kafka.KafkaTopics;
import com.loopers.interfaces.consumer.events.catalog.CatalogTopicMessage;
import com.loopers.interfaces.consumer.events.catalog.LikeProductCreated;
import com.loopers.interfaces.consumer.events.catalog.LikeProductDeleted;
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
    public void consumeCatalogTopicEvent(List<CatalogTopicMessage> messages, Acknowledgment acknowledgment) {
        for (CatalogTopicMessage message : messages) {
            switch (message.payload()) {
                case LikeProductCreated event ->
                        cacheEvictFacade.evictProductCache(event, message.eventId(), CacheEvictKafkaConfig.CACHE_EVICT_GROUP, message.producedAt());
                case LikeProductDeleted event ->
                        cacheEvictFacade.evictProductCache(event, message.eventId(), CacheEvictKafkaConfig.CACHE_EVICT_GROUP, message.producedAt());
                default -> {
                }
            }
        }
        acknowledgment.acknowledge();
    }
}
