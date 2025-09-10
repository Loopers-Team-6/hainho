package com.loopers.interfaces.consumer.metrics;

import com.loopers.application.metrics.ProductMetricsFacade;
import com.loopers.confg.kafka.KafkaTopics;
import com.loopers.interfaces.consumer.events.catalog.CatalogTopicMessage;
import com.loopers.interfaces.consumer.events.catalog.LikeProductCreated;
import com.loopers.interfaces.consumer.events.catalog.LikeProductDeleted;
import com.loopers.interfaces.consumer.events.catalog.ProductFound;
import com.loopers.interfaces.consumer.events.order.OrderCompleted;
import com.loopers.interfaces.consumer.events.order.OrderTopicMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MetricsConsumer {
    private final ProductMetricsFacade productMetricsFacade;

    @KafkaListener(
            topics = {KafkaTopics.ORDER},
            groupId = MetricsKafkaConfig.METRICS_GROUP,
            containerFactory = MetricsKafkaConfig.METRICS_LISTENER
    )
    public void consumeOrderTopicEvent(List<OrderTopicMessage> messages, Acknowledgment acknowledgment) {
        for (OrderTopicMessage message : messages) {
            switch (message.payload()) {
                case OrderCompleted event ->
                        productMetricsFacade.accumulatePurchases(event, message.eventId(), MetricsKafkaConfig.METRICS_GROUP, message.producedAt());
                default -> {
                }
            }
        }
        acknowledgment.acknowledge();
    }


    @KafkaListener(
            topics = {KafkaTopics.CATALOG},
            groupId = MetricsKafkaConfig.METRICS_GROUP,
            containerFactory = MetricsKafkaConfig.METRICS_LISTENER
    )
    public void consumeCatalogTopicEvent(List<CatalogTopicMessage> messages, Acknowledgment acknowledgment) {
        for (CatalogTopicMessage message : messages) {
            switch (message.payload()) {
                case LikeProductCreated event ->
                        productMetricsFacade.accumulateLikes(event, message.eventId(), MetricsKafkaConfig.METRICS_GROUP, message.producedAt());
                case LikeProductDeleted event ->
                        productMetricsFacade.accumulateLikes(event, message.eventId(), MetricsKafkaConfig.METRICS_GROUP, message.producedAt());
                case ProductFound event ->
                        productMetricsFacade.accumulateViews(event, message.eventId(), MetricsKafkaConfig.METRICS_GROUP, message.producedAt());
                default -> {
                }
            }
        }
        acknowledgment.acknowledge();
    }
}
