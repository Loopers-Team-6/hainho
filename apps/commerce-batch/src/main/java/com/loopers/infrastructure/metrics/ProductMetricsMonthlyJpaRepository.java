package com.loopers.infrastructure.metrics;

import com.loopers.domain.metrics.ProductMetricsMonthly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMetricsMonthlyJpaRepository extends JpaRepository<ProductMetricsMonthly, Long> {
}
