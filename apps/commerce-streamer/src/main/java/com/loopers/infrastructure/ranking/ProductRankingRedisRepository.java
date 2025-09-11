package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.ProductRankingRepository;
import com.loopers.domain.ranking.RankingInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


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
        incrementScoreAllAtOnce(productIdScoreMap, rankingKey);
    }

    private void incrementScoreAllAtOnce(Map<Long, Double> productIdScoreMap, String rankingKey) {
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) {
                RedisOperations<String, Object> ops = (RedisOperations<String, Object>) operations;
                productIdScoreMap.forEach((productId, score) ->
                        ops.opsForZSet().incrementScore(rankingKey, productId, score));
                return null;
            }
        });
    }

    @Override
    public List<RankingInfo.WithScore> getRankingAllWithScore(LocalDate date) {
        String key = generateKey(date);
        Set<ZSetOperations.TypedTuple<Object>> rankingTuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        if (rankingTuples == null) {
            return List.of();
        }
        return toRankingInfoList(rankingTuples);
    }

    private List<RankingInfo.WithScore> toRankingInfoList(Set<ZSetOperations.TypedTuple<Object>> rankingTuples) {
        return rankingTuples.stream()
                .map(tuple -> new RankingInfo.WithScore(
                        Long.valueOf(tuple.getValue().toString()),
                        tuple.getScore()))
                .toList();
    }

    @Override
    public void addRanking(LocalDate date, List<RankingInfo.WithScore> rankingInfos) {
        String rankingKey = generateKey(date);
        redisTemplate.opsForZSet().add(rankingKey, toTypedTuples(rankingInfos));
    }

    private Set<ZSetOperations.TypedTuple<Object>> toTypedTuples(List<RankingInfo.WithScore> rankingInfos) {
        return rankingInfos.stream()
                .map(info -> new DefaultTypedTuple<>((Object) info.productId(), info.score()))
                .collect(Collectors.toSet());
    }
}
