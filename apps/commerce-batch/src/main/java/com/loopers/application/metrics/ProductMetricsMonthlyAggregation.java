package com.loopers.application.metrics;

public record ProductMetricsMonthlyAggregation(
        Long productId,
        Long totalViews,
        Long totalPurchases,
        Long totalLikes,
        Double averageScore
) {
    public Double calculateScore() {
        if (totalViews == 0) return 0.0;
        double purchaseRate = (double) totalPurchases / totalViews;
        double likeRate = (double) totalLikes / totalViews;
        return purchaseRate * 0.7 + likeRate * 0.3;
    }
}