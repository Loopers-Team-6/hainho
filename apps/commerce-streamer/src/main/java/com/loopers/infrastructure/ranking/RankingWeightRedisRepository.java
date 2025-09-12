package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingTargetType;
import com.loopers.domain.ranking.RankingWeight;
import com.loopers.domain.ranking.WeightType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingWeightRedisRepository {
    private static final String KEY_PREFIX = "ranking_weight:";
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(RankingTargetType type, WeightType weightType, RankingWeight rankingWeight) {
        String key = buildKey(type, weightType);
        redisTemplate.opsForValue().set(key, rankingWeight);
        Date tomorrowDate = getTomorrowDate();
        redisTemplate.expireAt(key, tomorrowDate);
    }

    private String buildKey(RankingTargetType type, WeightType weightType) {
        return KEY_PREFIX + type.name() + ":" + weightType.name();
    }

    private Date getTomorrowDate() {
        ZonedDateTime tomorrow = ZonedDateTime.now().plusDays(1);
        return Date.from(tomorrow.toInstant());
    }

    public Optional<RankingWeight> findWeight(RankingTargetType type, WeightType weightType) {
        String key = buildKey(type, weightType);
        RankingWeight rankingWeight = (RankingWeight) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(rankingWeight);
    }
}
