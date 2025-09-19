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
public class WeeklyMetricsScheduler {

    private final JobLauncher jobLauncher;
    private final Job productMetricWeeklyJob;

    public WeeklyMetricsScheduler(JobLauncher jobLauncher,
                                  @Qualifier("productMetricWeeklyJob") Job productMetricWeeklyJob) {
        this.jobLauncher = jobLauncher;
        this.productMetricWeeklyJob = productMetricWeeklyJob;
    }

    @Scheduled(cron = "0 0 0 ? * MON")
    public void runWeeklyMetricsBatch() {
        try {
            LocalDate targetDate = LocalDate.now().minusDays(7);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("targetDate", targetDate.toString())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            log.info("Starting weekly metrics batch for date: {}", targetDate);
            jobLauncher.run(productMetricWeeklyJob, jobParameters);
            log.info("Weekly metrics batch completed successfully");

        } catch (Exception e) {
            log.error("Failed to run weekly metrics batch", e);
        }
    }
}
