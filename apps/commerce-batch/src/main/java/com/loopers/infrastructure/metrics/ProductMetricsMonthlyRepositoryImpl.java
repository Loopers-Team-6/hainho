package com.loopers.infrastructure.metrics;

import com.loopers.domain.metrics.ProductMetricsMonthly;
import com.loopers.domain.metrics.ProductMetricsMonthlyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetricsMonthlyRepositoryImpl implements ProductMetricsMonthlyRepository {
    private final ProductMetricsMonthlyJpaRepository jpaRepository;

    @Override
    public ProductMetricsMonthly save(ProductMetricsMonthly metrics) {
        return jpaRepository.save(metrics);
    }
}
