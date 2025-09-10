package com.loopers.application.cache;

import com.loopers.domain.cache.CacheEvictService;
import com.loopers.domain.event.EventHandledService;
import com.loopers.interfaces.consumer.events.catalog.LikeProductCreated;
import com.loopers.interfaces.consumer.events.catalog.LikeProductDeleted;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CacheEvictFacade {
    private final EventHandledService eventHandledService;
    private final CacheEvictService cacheEvictService;

    @Transactional
    public void evictProductCache(LikeProductCreated event, String eventId, String groupId, ZonedDateTime producedAt) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        cacheEvictService.evictProductCache(event.productId());
    }

    @Transactional
    public void evictProductCache(LikeProductDeleted event, String eventId, String groupId, ZonedDateTime producedAt) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        cacheEvictService.evictProductCache(event.productId());
    }
}
