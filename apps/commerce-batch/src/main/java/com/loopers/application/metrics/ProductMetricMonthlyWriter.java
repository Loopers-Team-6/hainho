package com.loopers.application.metrics;

import com.loopers.infrastructure.metrics.ProductMetricsMonthlyRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class ProductMetricMonthlyWriter implements ItemWriter<List<ProductMetricsMonthlyAggregation>> {

    private final ProductMetricsMonthlyRepositoryImpl repository;

    @Value("#{jobParameters['targetDate'] ?: T(java.time.LocalDate).now().toString()}")
    private String targetDateStr;

    @Override
    public void write(Chunk<? extends List<ProductMetricsMonthlyAggregation>> chunk) throws Exception {
        LocalDate targetDate = LocalDate.parse(targetDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        YearMonth targetMonth = YearMonth.from(targetDate);
        long monthNumber = targetMonth.getMonthValue();

        List<MonthlyMetricsBatch> batchData = chunk.getItems().stream()
                .flatMap(List::stream)
                .map(aggregation -> new MonthlyMetricsBatch(
                        aggregation.productId(),
                        monthNumber,
                        aggregation.totalViews(),
                        aggregation.totalPurchases(),
                        aggregation.totalLikes(),
                        aggregation.calculateScore()
                ))
                .toList();

        repository.bulkUpsertWithIncrement(batchData);
    }
}