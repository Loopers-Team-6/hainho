package com.loopers.infrastructure.metrics;

import com.loopers.application.metrics.ProductMetricsWeeklyAggregation;
import com.loopers.domain.metrics.ProductMetrics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetrics, Long> {
    List<ProductMetrics> findByMeasuredDateBetween(LocalDate startDate, LocalDate endDate);

    Page<ProductMetrics> findByMeasuredDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT DISTINCT p.productId FROM ProductMetrics p WHERE p.measuredDate BETWEEN :startDate AND :endDate ORDER BY p.productId")
    List<Long> findDistinctProductIds(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT new com.loopers.application.metrics.ProductMetricsWeeklyAggregation(
                p.productId,
                SUM(p.views),
                SUM(p.purchases),
                SUM(p.likes)
            )
            FROM ProductMetrics p
            WHERE p.productId IN :productIds AND p.measuredDate BETWEEN :startDate AND :endDate
            GROUP BY p.productId
            ORDER BY p.productId
            """)
    List<ProductMetricsWeeklyAggregation> findAggregatedByProductIds(
            @Param("productIds") List<Long> productIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}