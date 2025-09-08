package com.loopers.application.metrics;

import com.loopers.domain.event.EventHandledService;
import com.loopers.domain.metrics.ProductMetricsService;
import com.loopers.interfaces.consumer.audit.OrderCompleted;
import com.loopers.interfaces.consumer.metrics.LikeProductCreated;
import com.loopers.interfaces.consumer.metrics.LikeProductDeleted;
import com.loopers.interfaces.consumer.metrics.ProductFound;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetricsFacade {
    private final EventHandledService eventHandledService;
    private final ProductMetricsService productMetricsService;

    @Transactional
    public void accumulateViews(ProductFound event, String eventId, String groupId, ZonedDateTime processedAt) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        productMetricsService.accumulateViews(event.productId(), 1, processedAt);
    }

    @Transactional
    public void accumulatePurchases(OrderCompleted event, String eventId, String groupId, ZonedDateTime processedAt) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        event.items().forEach(item ->
                productMetricsService.accumulatePurchases(item.productId(), item.quantity(), processedAt)
        );
    }

    @Transactional
    public void accumulateLikes(LikeProductCreated event, String eventId, String groupId, ZonedDateTime processedAt) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        productMetricsService.accumulateLikes(event.productId(), 1, processedAt);
    }

    @Transactional
    public void accumulateLikes(LikeProductDeleted event, String eventId, String groupId, ZonedDateTime processedAt) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        productMetricsService.accumulateLikes(event.productId(), -1, processedAt);
    }
}
