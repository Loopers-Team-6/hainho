package com.loopers.application.ranking;

import com.loopers.domain.ranking.ProductRankingService;
import com.loopers.interfaces.consumer.events.catalog.LikeProductCreated;
import com.loopers.interfaces.consumer.events.catalog.LikeProductDeleted;
import com.loopers.interfaces.consumer.events.catalog.ProductFound;
import com.loopers.interfaces.consumer.events.order.OrderCompleted;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingFacade {
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private final ProductRankingService productRankingService;

    private LocalDate toZoneDate(ZonedDateTime producedAt) {
        return producedAt.withZoneSameInstant(ZONE_ID).toLocalDate();
    }

    public void rankByViews(ProductFound event, ZonedDateTime producedAt) {
        productRankingService.rankByViews(event.productId(), 1L, toZoneDate(producedAt));
    }

    public void rankByPurchases(OrderCompleted event, ZonedDateTime producedAt) {
        event.items().forEach(item ->
                productRankingService.rankByPurchases(item.productId(), item.price(), item.quantity(), toZoneDate(producedAt))
        );
    }

    public void rankByLikes(LikeProductCreated event, ZonedDateTime producedAt) {
        productRankingService.rankByLikes(event.productId(), 1L, toZoneDate(producedAt));
    }

    public void rankByUnlikes(LikeProductDeleted event, ZonedDateTime producedAt) {
        productRankingService.rankByLikes(event.productId(), -1L, toZoneDate(producedAt));
    }
}
