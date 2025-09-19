package com.loopers.application.metrics;

public record WeeklyMetricsBatch(
        Long productId,
        Long weekNumber,
        Long views,
        Long purchases,
        Long likes,
        Double score
) {
}