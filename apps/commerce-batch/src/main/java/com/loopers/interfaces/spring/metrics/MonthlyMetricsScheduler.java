package com.loopers.interfaces.spring.metrics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class MonthlyMetricsScheduler {

    private final JobLauncher jobLauncher;
    private final Job productMetricMonthlyJob;

    public MonthlyMetricsScheduler(JobLauncher jobLauncher,
                                   @Qualifier("productMetricMonthlyJob") Job productMetricMonthlyJob) {
        this.jobLauncher = jobLauncher;
        this.productMetricMonthlyJob = productMetricMonthlyJob;
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void runMonthlyMetricsBatch() {
        try {
            LocalDate targetDate = LocalDate.now().minusMonths(1);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("targetDate", targetDate.toString())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            log.info("Starting monthly metrics batch for date: {}", targetDate);
            jobLauncher.run(productMetricMonthlyJob, jobParameters);
            log.info("Monthly metrics batch completed successfully");

        } catch (Exception e) {
            log.error("Failed to run monthly metrics batch", e);
        }
    }
}