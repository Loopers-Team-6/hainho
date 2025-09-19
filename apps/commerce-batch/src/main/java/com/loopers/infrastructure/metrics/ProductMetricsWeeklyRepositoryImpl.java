package com.loopers.infrastructure.metrics;

import com.loopers.domain.metrics.ProductMetricsWeekly;
import com.loopers.domain.metrics.ProductMetricsWeeklyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetricsWeeklyRepositoryImpl implements ProductMetricsWeeklyRepository {
    private final ProductMetricsWeeklyJpaRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public ProductMetricsWeekly save(ProductMetricsWeekly metrics) {
        return jpaRepository.save(metrics);
    }

    public void bulkUpsertWithIncrement(List<com.loopers.application.metrics.WeeklyMetricsBatch> batchData) {
        List<Object[]> batchArgs = batchData.stream()
                .map(data -> new Object[]{
                        data.productId(),
                        data.weekNumber(),
                        data.views(),
                        data.purchases(),
                        data.likes(),
                        data.score()
                })
                .toList();

        String sql = """
                INSERT INTO product_metrics_weekly
                    (product_id, measured_week, views, purchases, likes, score, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())
                ON DUPLICATE KEY UPDATE
                    views      = views      + VALUES(views),
                    purchases  = purchases  + VALUES(purchases),
                    likes      = likes      + VALUES(likes),
                    score      = (purchases + VALUES(purchases)) * 10.0 + (likes + VALUES(likes)) * 2.0 + (views + VALUES(views)) * 0.1,
                    updated_at = NOW()
                """;

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
