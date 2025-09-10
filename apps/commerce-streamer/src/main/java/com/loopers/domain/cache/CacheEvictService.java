package com.loopers.domain.cache;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CacheEvictService {
    public void evictProductCache(Long productId) {
        // Implementation to evict product cache
    }
}
