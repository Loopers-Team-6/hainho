package com.loopers.infrastructure.metrics;

import com.loopers.application.metrics.ProductMetricsMonthlyAggregation;
import com.loopers.domain.metrics.ProductMetricsWeekly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductMetricsWeeklyJpaRepository extends JpaRepository<ProductMetricsWeekly, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            INSERT INTO product_metrics_weekly
                (product_id, measured_week, views, purchases, likes, score, created_at, updated_at)
            VALUES
                (:productId, :measuredWeek, :views, :purchases, :likes, :score, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
                views      = views      + :views,
                purchases  = purchases  + :purchases,
                likes      = likes      + :likes,
                score      = (purchases + :purchases) * 10.0 + (likes + :likes) * 2.0 + (views + :views) * 0.1,
                updated_at = NOW()
            """, nativeQuery = true)
    int upsertWeeklyMetrics(
            @Param("productId") Long productId,
            @Param("measuredWeek") Long measuredWeek,
            @Param("views") Long views,
            @Param("purchases") Long purchases,
            @Param("likes") Long likes,
            @Param("score") Double score
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            INSERT INTO product_metrics_weekly
                (product_id, measured_week, views, purchases, likes, score, created_at, updated_at)
            VALUES :valuesClause
            ON DUPLICATE KEY UPDATE
                views      = views      + VALUES(views),
                purchases  = purchases  + VALUES(purchases),
                likes      = likes      + VALUES(likes),
                score      = (purchases + VALUES(purchases)) * 10.0 + (likes + VALUES(likes)) * 2.0 + (views + VALUES(views)) * 0.1,
                updated_at = NOW()
            """, nativeQuery = true)
    int bulkUpsertWeeklyMetrics(@Param("valuesClause") String valuesClause);

    @Query("""
            SELECT new com.loopers.application.metrics.ProductMetricsMonthlyAggregation(
                pmw.productId,
                SUM(pmw.views),
                SUM(pmw.purchases),
                SUM(pmw.likes),
                AVG(pmw.score)
            )
            FROM ProductMetricsWeekly pmw
            WHERE pmw.productId IN :productIds
              AND pmw.measuredWeek >= :startWeek
              AND pmw.measuredWeek <= :endWeek
            GROUP BY pmw.productId
            """)
    List<ProductMetricsMonthlyAggregation> findAggregatedMonthlyByProductIds(
            @Param("productIds") List<Long> productIds,
            @Param("startWeek") Long startWeek,
            @Param("endWeek") Long endWeek
    );

    @Query("""
            SELECT DISTINCT pmw.productId
            FROM ProductMetricsWeekly pmw
            WHERE pmw.measuredWeek >= :startWeek
              AND pmw.measuredWeek <= :endWeek
            """)
    List<Long> findDistinctProductIds(
            @Param("startWeek") Long startWeek,
            @Param("endWeek") Long endWeek
    );
}
