package com.loopers.application.metrics;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class ProductMetricMonthlyJobConfig {
    public static final String TRIGGER_NAME = "ProductMetricMonthlyTrigger";
    public static final String CRON_EXPRESSION = "0 0 0 1 * ?"; // Every 1st day of month at midnight
    private static final String JOB_NAME = "ProductMetricMonthlyJob";
    private static final Integer CHUNK_SIZE = 1000;

    @Bean
    public Job productMetricMonthlyJob(JobRepository jobRepository, Step productMetricMonthlyStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(productMetricMonthlyStep)
                .build();
    }

    @Bean
    public Step productMetricMonthlyStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ProductMetricMonthlyReader reader,
            ProductMetricMonthlyWriter writer
    ) {
        return new StepBuilder("ProductMetricMonthlyStep", jobRepository)
                .<List<ProductMetricsMonthlyAggregation>, List<ProductMetricsMonthlyAggregation>>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }
}