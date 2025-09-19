package com.loopers.application.metrics;

public record ProductMetricsWeeklyAggregation(
        Long productId,
        Long views,
        Long purchases,
        Long likes
) {
    public double calculateScore() {
        return (purchases * 10.0) + (likes * 2.0) + (views * 0.1);
    }
}
