package com.loopers.application.ranking;

import com.loopers.domain.ranking.ProductRankingService;
import com.loopers.domain.ranking.RankingInfo;
import com.loopers.interfaces.consumer.events.catalog.CatalogTopicMessage;
import com.loopers.interfaces.consumer.events.catalog.LikeProductCreated;
import com.loopers.interfaces.consumer.events.catalog.LikeProductDeleted;
import com.loopers.interfaces.consumer.events.catalog.ProductFound;
import com.loopers.interfaces.consumer.events.order.OrderCompleted;
import com.loopers.interfaces.consumer.events.order.OrderTopicMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingFacade {
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private final ProductRankingService productRankingService;

    private LocalDate toZoneDate(ZonedDateTime producedAt) {
        return producedAt.withZoneSameInstant(ZONE_ID).toLocalDate();
    }

    public void rankByCatalogEvent(List<CatalogTopicMessage> messages) {
        Map<Long, Double> productIdScoreMap = toProductIdScoreMapFromCatalogMessages(messages);
        LocalDate producedDate = toZoneDate(messages.get(0).producedAt());
        productRankingService.rank(productIdScoreMap, producedDate);
    }

    private Map<Long, Double> toProductIdScoreMapFromCatalogMessages(List<CatalogTopicMessage> messages) {
        List<ProductFound> productFoundEvents = messages.stream()
                .map(CatalogTopicMessage::payload)
                .filter(ProductFound.class::isInstance)
                .map(ProductFound.class::cast)
                .toList();

        List<LikeProductCreated> likeCreatedEvents = messages.stream()
                .map(CatalogTopicMessage::payload)
                .filter(LikeProductCreated.class::isInstance)
                .map(LikeProductCreated.class::cast)
                .toList();

        List<LikeProductDeleted> likeDeletedEvents = messages.stream()
                .map(CatalogTopicMessage::payload)
                .filter(LikeProductDeleted.class::isInstance)
                .map(LikeProductDeleted.class::cast)
                .toList();

        Map<Long, Double> productIdScoreMap = new HashMap<>();

        productFoundEvents.stream()
                .collect(Collectors.groupingBy(
                        ProductFound::productId,
                        Collectors.counting()
                ))
                .forEach((productId, count) -> {
                    double score = productRankingService.calculateScoreByView(count);
                    productIdScoreMap.merge(productId, score, Double::sum);
                });

        likeCreatedEvents.stream()
                .collect(Collectors.groupingBy(
                        LikeProductCreated::productId,
                        Collectors.counting()
                ))
                .forEach((productId, count) -> {
                    double score = productRankingService.calculateScoreByLikeCreated(count);
                    productIdScoreMap.merge(productId, score, Double::sum);
                });

        likeDeletedEvents.stream()
                .collect(Collectors.groupingBy(
                        LikeProductDeleted::productId,
                        Collectors.counting()
                ))
                .forEach((productId, count) -> {
                    double score = productRankingService.calculateScoreByLikeDeleted(count);
                    productIdScoreMap.merge(productId, score, Double::sum);
                });

        return productIdScoreMap;
    }

    public void rankByOrderEvent(List<OrderTopicMessage> messages) {
        Map<Long, Double> productIdScoreMap = toProductIdScoreMapFromOrderMessages(messages);
        LocalDate producedDate = toZoneDate(messages.get(0).producedAt());
        productRankingService.rank(productIdScoreMap, producedDate);
    }

    private Map<Long, Double> toProductIdScoreMapFromOrderMessages(List<OrderTopicMessage> messages) {
        List<OrderCompleted> orderCompletedEvents =
                messages.stream()
                        .map(OrderTopicMessage::payload)
                        .filter(OrderCompleted.class::isInstance)
                        .map(OrderCompleted.class::cast)
                        .toList();

        return orderCompletedEvents.stream()
                .flatMap(order -> order.items().stream())
                .collect(
                        Collectors.groupingBy(
                                OrderCompleted.OrderItem::productId,
                                Collectors.summingDouble(item -> productRankingService.calculateScoreByPriceAndAmount(item.price(), item.quantity()))
                        )
                );
    }

    public void carryOverDailyRanking(LocalDate currentDate) {
        LocalDate nextDate = currentDate.plusDays(1);
        List<RankingInfo.WithScore> rankingInfos = productRankingService.getRankingAll(currentDate);
        productRankingService.addRanking(nextDate, rankingInfos);
    }
}
