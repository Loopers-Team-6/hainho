package com.loopers.application.metrics;

public record MonthlyMetricsBatch(
        Long productId,
        Long monthNumber,
        Long totalViews,
        Long totalPurchases,
        Long totalLikes,
        Double averageScore
) {
}