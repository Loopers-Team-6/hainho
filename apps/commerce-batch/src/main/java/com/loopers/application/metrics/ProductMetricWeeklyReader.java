package com.loopers.application.metrics;

import com.loopers.infrastructure.metrics.ProductMetricsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class ProductMetricWeeklyReader implements ItemReader<List<ProductMetricsWeeklyAggregation>> {

    private final ProductMetricsJpaRepository repository;

    @Value("#{jobParameters['targetDate'] ?: T(java.time.LocalDate).now().toString()}")
    private String targetDateStr;

    @Value("${batch.chunk-size:1000}")
    private int chunkSize;

    private List<Long> productIds;
    private int currentIndex = 0;
    private boolean initialized = false;

    @Override
    public List<ProductMetricsWeeklyAggregation> read() throws Exception {
        if (!initialized) {
            initializeProductIds();
            initialized = true;
        }

        if (currentIndex >= productIds.size()) {
            return null; // End of data
        }

        int endIndex = Math.min(currentIndex + chunkSize, productIds.size());
        List<Long> chunkProductIds = productIds.subList(currentIndex, endIndex);
        currentIndex = endIndex;

        LocalDate targetDate = LocalDate.parse(targetDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate weekStart = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        return repository.findAggregatedByProductIds(chunkProductIds, weekStart, weekEnd);
    }

    private void initializeProductIds() {
        LocalDate targetDate = LocalDate.parse(targetDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate weekStart = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        this.productIds = repository.findDistinctProductIds(weekStart, weekEnd);
    }
}