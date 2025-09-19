package com.loopers.domain.metrics;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "product_metrics",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_metrics_product_id_measured_date",
                        columnNames = {"productId", "measured_date"}
                )
        }
)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class ProductMetrics extends BaseEntity {
    private Long productId;
    private Long views;
    private Long purchases;
    private Long likes;
    private LocalDate measuredDate;

    public static ProductMetrics of(Long productId, Long views, Long purchases, Long likes, LocalDate measuredDate) {
        ProductMetrics productMetrics = new ProductMetrics();
        productMetrics.productId = productId;
        productMetrics.views = views;
        productMetrics.purchases = purchases;
        productMetrics.likes = likes;
        productMetrics.measuredDate = measuredDate;
        return productMetrics;
    }
}