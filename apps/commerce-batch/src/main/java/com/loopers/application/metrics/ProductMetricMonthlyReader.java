package com.loopers.application.metrics;

import com.loopers.infrastructure.metrics.ProductMetricsWeeklyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class ProductMetricMonthlyReader implements ItemReader<List<ProductMetricsMonthlyAggregation>> {

    private final ProductMetricsWeeklyJpaRepository weeklyRepository;

    @Value("#{jobParameters['targetDate'] ?: T(java.time.LocalDate).now().toString()}")
    private String targetDateStr;

    @Value("${batch.chunk-size:1000}")
    private int chunkSize;

    private List<Long> productIds;
    private int currentIndex = 0;
    private boolean initialized = false;

    @Override
    public List<ProductMetricsMonthlyAggregation> read() throws Exception {
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
        YearMonth targetMonth = YearMonth.from(targetDate);
        LocalDate monthStart = targetMonth.atDay(1);
        LocalDate monthEnd = targetMonth.atEndOfMonth();

        Long startWeek = (long) monthStart.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        Long endWeek = (long) monthEnd.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

        return weeklyRepository.findAggregatedMonthlyByProductIds(chunkProductIds, startWeek, endWeek);
    }

    private void initializeProductIds() {
        LocalDate targetDate = LocalDate.parse(targetDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        YearMonth targetMonth = YearMonth.from(targetDate);
        LocalDate monthStart = targetMonth.atDay(1);
        LocalDate monthEnd = targetMonth.atEndOfMonth();

        Long startWeek = (long) monthStart.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        Long endWeek = (long) monthEnd.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

        this.productIds = weeklyRepository.findDistinctProductIds(startWeek, endWeek);
    }
}