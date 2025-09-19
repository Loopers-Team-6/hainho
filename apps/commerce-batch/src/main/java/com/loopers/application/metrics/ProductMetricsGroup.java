package com.loopers.application.metrics;

import com.loopers.domain.metrics.ProductMetrics;

import java.util.List;

public record ProductMetricsGroup(
        Long productId,
        List<ProductMetrics> dailyMetrics
) {
}