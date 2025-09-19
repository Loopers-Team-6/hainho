package com.loopers.application.metrics;

import com.loopers.infrastructure.metrics.ProductMetricsWeeklyRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class ProductMetricWeeklyWriter implements ItemWriter<List<ProductMetricsWeeklyAggregation>> {

    private final ProductMetricsWeeklyRepositoryImpl repository;

    @Value("#{jobParameters['targetDate'] ?: T(java.time.LocalDate).now().toString()}")
    private String targetDateStr;

    @Override
    public void write(Chunk<? extends List<ProductMetricsWeeklyAggregation>> chunk) throws Exception {
        LocalDate targetDate = LocalDate.parse(targetDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate weekStart = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        long weekNumber = weekStart.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

        List<WeeklyMetricsBatch> batchData = chunk.getItems().stream()
                .flatMap(List::stream)
                .map(aggregation -> new WeeklyMetricsBatch(
                        aggregation.productId(),
                        weekNumber,
                        aggregation.views(),
                        aggregation.purchases(),
                        aggregation.likes(),
                        aggregation.calculateScore()
                ))
                .toList();

        repository.bulkUpsertWithIncrement(batchData);
    }
}