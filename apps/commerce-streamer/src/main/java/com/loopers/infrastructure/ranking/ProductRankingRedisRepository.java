package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.ProductRankingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;


@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRankingRedisRepository implements ProductRankingRepository {
    private static final String PRODUCT_RANKING_KEY_PREFIX = "rank:all:";

    private final RedisTemplate<String, Object> redisTemplate;

    private String generateKey(LocalDate actionedAt) {
        return PRODUCT_RANKING_KEY_PREFIX + actionedAt.toString();
    }

    @Override
    public void incrementScore(Map<Long, Double> productIdScoreMap, LocalDate actionedAt) {
        String rankingKey = generateKey(actionedAt);
        productIdScoreMap.forEach((productId, score) -> {
            redisTemplate.opsForZSet().incrementScore(rankingKey, productId, score);
        });
    }
}
