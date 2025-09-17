package com.loopers.infrastructure.metrics;

import com.loopers.domain.metrics.ProductMetricsWeekly;
import com.loopers.domain.metrics.ProductMetricsWeeklyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetricsWeeklyRepositoryImpl implements ProductMetricsWeeklyRepository {
    private final ProductMetricsWeeklyJpaRepository jpaRepository;

    @Override
    public ProductMetricsWeekly save(ProductMetricsWeekly metrics) {
        return jpaRepository.save(metrics);
    }
}
