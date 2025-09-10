package com.loopers.infrastructure.product;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductPageTotalCountRedisTemplate {
    private static final String PRODUCT_PAGE_TOTAL_COUNT_KEY_PREFIX = "ProductPageTotalCount:v1:brandId:";
    private static final Long TTL_MINUTES = 1L;

    private final RedisTemplate<String, Object> redisTemplate;

    private String generateKey(Long brandId) {
        if (brandId == null) {
            return PRODUCT_PAGE_TOTAL_COUNT_KEY_PREFIX + "null";
        }
        return PRODUCT_PAGE_TOTAL_COUNT_KEY_PREFIX + brandId;
    }

    public void set(Long brandId, Long value) {
        String key = generateKey(brandId);
        redisTemplate.opsForValue().set(key, value, TTL_MINUTES, java.util.concurrent.TimeUnit.MINUTES);
    }

    public Optional<Long> find(Long brandId) {
        String key = generateKey(brandId);
        Object raw = redisTemplate.opsForValue().get(key);
        if (raw == null) {
            return Optional.empty();
        }
        Long count = (Long) raw;
        return Optional.ofNullable(count);
    }

    public void delete(Long brandId) {
        String key = generateKey(brandId);
        redisTemplate.delete(key);
    }
}
