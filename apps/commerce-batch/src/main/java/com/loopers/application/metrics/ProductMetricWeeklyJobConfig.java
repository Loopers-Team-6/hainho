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
public class ProductMetricWeeklyJobConfig {
    public static final String TRIGGER_NAME = "ProductMetricWeeklyTrigger";
    public static final String CRON_EXPRESSION = "0 0 0 ? * MON"; // Every Monday at midnight
    private static final String JOB_NAME = "ProductMetricWeeklyJob";
    private static final Integer CHUNK_SIZE = 1000;

    @Bean
    public Job productMetricWeeklyJob(JobRepository jobRepository, Step productMetricWeeklyStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(productMetricWeeklyStep)
                .build();
    }

    @Bean
    public Step productMetricWeeklyStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ProductMetricWeeklyReader reader,
            ProductMetricWeeklyWriter writer
    ) {
        return new StepBuilder("ProductMetricWeeklyStep", jobRepository)
                .<List<ProductMetricsWeeklyAggregation>, List<ProductMetricsWeeklyAggregation>>chunk(CHUNK_SIZE, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

}
