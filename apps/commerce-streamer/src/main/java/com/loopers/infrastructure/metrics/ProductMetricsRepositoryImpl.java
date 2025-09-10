package com.loopers.infrastructure.metrics;

import com.loopers.domain.metrics.ProductMetricsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetricsRepositoryImpl implements ProductMetricsRepository {
    private final ProductMetricsJpaRepository productMetricsJpaRepository;

    @Override
    public int upsertAndAccumulate(Long productId, LocalDate measuredDate, long views, long purchases, long likes) {
        return productMetricsJpaRepository.upsertAndAccumulate(productId, measuredDate, views, purchases, likes);
    }
}
