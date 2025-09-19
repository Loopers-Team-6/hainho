package com.loopers.infrastructure.metrics;

import com.loopers.domain.metrics.ProductMetrics;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetrics, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            INSERT INTO product_metrics
                (product_id, measured_date, views, purchases, likes, created_at, updated_at)
            VALUES
                (:productId, :measuredDate, :viewsDelta, :purchasesDelta, :likesDelta, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
                views      = views      + :viewsDelta,
                purchases  = purchases  + :purchasesDelta,
                likes      = likes      + :likesDelta,
                updated_at = NOW()
            """, nativeQuery = true)
    int upsertAndAccumulate(
            @Param("productId") Long productId,
            @Param("measuredDate") LocalDate measuredDate,
            @Param("viewsDelta") long viewsDelta,
            @Param("purchasesDelta") long purchasesDelta,
            @Param("likesDelta") long likesDelta
    );

    List<ProductMetrics> findByMeasuredDateBetween(LocalDate startDate, LocalDate endDate);
}
