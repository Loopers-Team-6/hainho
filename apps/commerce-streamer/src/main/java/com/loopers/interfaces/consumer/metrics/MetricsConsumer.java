package com.loopers.interfaces.consumer.metrics;

import com.loopers.application.metrics.ProductMetricsFacade;
import com.loopers.confg.kafka.KafkaTopics;
import com.loopers.interfaces.consumer.KafkaMessage;
import com.loopers.interfaces.consumer.audit.OrderCompleted;
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
            topics = {KafkaTopics.CATALOG, KafkaTopics.ORDER},
            groupId = MetricsKafkaConfig.METRICS_GROUP,
            containerFactory = MetricsKafkaConfig.METRICS_LISTENER
    )
    public void consume(List<KafkaMessage<?>> messages, Acknowledgment acknowledgment) {
        for (KafkaMessage<?> message : messages) {
            switch (message.eventType()) {
                case "OrderCompleted" -> {
                    OrderCompleted event = (OrderCompleted) message.payload();
                    productMetricsFacade.accumulatePurchases(event, message.eventId(), MetricsKafkaConfig.METRICS_GROUP, message.producedAt());
                }
                case "LikeProductCreated" -> {
                    LikeProductCreated event = (LikeProductCreated) message.payload();
                    productMetricsFacade.accumulateLikes(event, message.eventId(), MetricsKafkaConfig.METRICS_GROUP, message.producedAt());
                }
                case "LikeProductDeleted" -> {
                    LikeProductDeleted event = (LikeProductDeleted) message.payload();
                    productMetricsFacade.accumulateLikes(event, message.eventId(), MetricsKafkaConfig.METRICS_GROUP, message.producedAt());
                }
                case "ProductFound" -> {
                    ProductFound event = (ProductFound) message.payload();
                    productMetricsFacade.accumulateViews(event, message.eventId(), MetricsKafkaConfig.METRICS_GROUP, message.producedAt());
                }
                default -> {
                }
            }
        }
        acknowledgment.acknowledge();
    }
}
