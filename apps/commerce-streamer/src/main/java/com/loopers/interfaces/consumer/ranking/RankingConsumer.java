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

import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingConsumer {
    private final RankingFacade rankingFacade;

    @KafkaListener(
            topics = {KafkaTopics.ORDER},
            groupId = RankingKafkaConfig.RANKING_GROUP,
            containerFactory = RankingKafkaConfig.RANKING_LISTENER
    )
    public void consumeOrderTopicEvent(List<OrderTopicMessage> messages, Acknowledgment acknowledgment) {
        List<OrderTopicMessage> orderCompletedMessages = messages.stream().filter(message -> message.payload() instanceof OrderCompleted).toList();
        if (orderCompletedMessages.isEmpty()) {
            acknowledgment.acknowledge();
            return;
        }
        rankingFacade.rankByOrderEvent(orderCompletedMessages);
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
        rankingFacade.carryOverDailyRanking(ZonedDateTime.now().toLocalDate());
        acknowledgment.acknowledge();
    }
}
