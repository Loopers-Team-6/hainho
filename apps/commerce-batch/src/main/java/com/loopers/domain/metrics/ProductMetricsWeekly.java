package com.loopers.domain.metrics;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "product_metrics_weekly",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_metrics_weekly_product_id_measured_week",
                        columnNames = {"productId", "measured_week"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductMetricsWeekly extends BaseEntity {
    private Long productId;
    private Long views;
    private Long purchases;
    private Long likes;
    private Double score;
    private Long measuredWeek;

    private ProductMetricsWeekly(Long productId, Long views, Long purchases, Long likes, Double score, Long measuredWeek) {
        this.productId = productId;
        this.views = views;
        this.purchases = purchases;
        this.likes = likes;
        this.score = score;
        this.measuredWeek = measuredWeek;
    }

    public static ProductMetricsWeekly of(Long productId, Long views, Long purchases, Long likes, Double score, Long measuredWeek) {
        return new ProductMetricsWeekly(productId, views, purchases, likes, score, measuredWeek);
    }
}
