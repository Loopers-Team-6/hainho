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
        name = "product_metrics_monthly",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_metrics_monthly_product_id_measured_month",
                        columnNames = {"productId", "measured_month"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductMetricsMonthly extends BaseEntity {
    private Long productId;
    private Long views;
    private Long purchases;
    private Long likes;
    private Double score;
    private Long measuredMonth;

    private ProductMetricsMonthly(Long productId, Long views, Long purchases, Long likes, Double score, Long measuredMonth) {
        this.productId = productId;
        this.views = views;
        this.purchases = purchases;
        this.likes = likes;
        this.score = score;
        this.measuredMonth = measuredMonth;
    }

    public static ProductMetricsMonthly of(Long productId, Long views, Long purchases, Long likes, Double score, Long measuredMonth) {
        return new ProductMetricsMonthly(productId, views, purchases, likes, score, measuredMonth);
    }
}
