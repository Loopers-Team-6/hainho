package com.loopers.infrastructure.metrics;

import com.loopers.domain.metrics.ProductMetricsWeekly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMetricsWeeklyJpaRepository extends JpaRepository<ProductMetricsWeekly, Long> {
}
