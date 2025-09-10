package com.loopers.domain.metrics;

import java.time.LocalDate;

public interface ProductMetricsRepository {
    int upsertAndAccumulate(Long productId, LocalDate measuredDate, long views, long purchases, long likes);
}
