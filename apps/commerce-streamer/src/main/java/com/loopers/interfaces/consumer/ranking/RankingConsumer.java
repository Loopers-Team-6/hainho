package com.loopers.interfaces.consumer.ranking;

import com.loopers.application.ranking.RankingFacade;
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
public class RankingConsumer {
    private final RankingFacade rankingFacade;

    @KafkaListener(
            topics = {KafkaTopics.ORDER},
            groupId = RankingCriticalKafkaConfig.RANKING_CRITICAL_GROUP,
            containerFactory = RankingCriticalKafkaConfig.RANKING_CRITICAL_LISTENER
    )
    public void consumeOrderTopicEvent(OrderTopicMessage message, Acknowledgment acknowledgment) {
        if (!(message.payload() instanceof OrderCompleted)) {
            acknowledgment.acknowledge();
            return;
        }
        rankingFacade.rankByOrderEvent(message, RankingCriticalKafkaConfig.RANKING_CRITICAL_GROUP);
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = {KafkaTopics.CATALOG},
            groupId = RankingKafkaConfig.RANKING_GROUP,
            containerFactory = RankingKafkaConfig.RANKING_LISTENER
    )
    public void consumeCatalogTopicEvent(List<CatalogTopicMessage> messages, Acknowledgment acknowledgment) {
        List<CatalogTopicMessage> catalogTopicMessages = messages.stream()
                .filter(message -> message.payload() instanceof ProductFound
                        || message.payload() instanceof LikeProductCreated
                        || message.payload() instanceof LikeProductDeleted)
                .toList();
        if (catalogTopicMessages.isEmpty()) {
            acknowledgment.acknowledge();
            return;
        }
        rankingFacade.rankByCatalogEvent(messages);
        acknowledgment.acknowledge();
    }
}
