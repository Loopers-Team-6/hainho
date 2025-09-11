package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.ProductRankingRepository;
import com.loopers.domain.ranking.RankingInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductRankingRedisRepository implements ProductRankingRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<RankingInfo.Get.Item> getRankingWithPagination(String date, Pageable pageable) {
        List<Object> rankingData = findRankingData(date, pageable);

        if (rankingData.isEmpty()) {
            return List.of();
        }

        return rankingData.stream()
                .map(value -> new RankingInfo.Get.Item(
                        Long.valueOf(value.toString())
                ))
                .toList();
    }

    private List<Object> findRankingData(String date, Pageable pageable) {
        String key = generateKey(date);
        long size = pageable.getPageSize();
        long start = pageable.getOffset();
        long end = start + size - 1;

        var result = redisTemplate.opsForZSet().reverseRange(key, start, end);
        return result == null ? List.of() : new ArrayList<>(result);
    }

    @Override
    public Long getTotalCount(String date) {
        String key = generateKey(date);
        return redisTemplate.opsForZSet().size(key);
    }

    private String generateKey(String date) {
        return "rank:all:" + date;
    }
}