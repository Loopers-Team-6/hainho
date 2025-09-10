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
            groupId = RankingKafkaConfig.RANKING_GROUP,
            containerFactory = RankingKafkaConfig.RANKING_LISTENER
    )
    public void consumeOrderTopicEvent(List<OrderTopicMessage> messages, Acknowledgment acknowledgment) {
        for (OrderTopicMessage message : messages) {
            switch (message.payload()) {
                case OrderCompleted event -> rankingFacade.rankByPurchases(event, message.producedAt());
                default -> {
                }
            }
        }
        acknowledgment.acknowledge();
    }


    @KafkaListener(
            topics = {KafkaTopics.CATALOG},
            groupId = RankingKafkaConfig.RANKING_GROUP,
            containerFactory = RankingKafkaConfig.RANKING_LISTENER
    )
    public void consumeCatalogTopicEvent(List<CatalogTopicMessage> messages, Acknowledgment acknowledgment) {
        for (CatalogTopicMessage message : messages) {
            switch (message.payload()) {
                case LikeProductCreated event -> rankingFacade.rankByLikes(event, message.producedAt());
                case LikeProductDeleted event -> rankingFacade.rankByUnlikes(event, message.producedAt());
                case ProductFound event -> rankingFacade.rankByViews(event, message.producedAt());
                default -> {
                }
            }
        }
        acknowledgment.acknowledge();
    }
}
