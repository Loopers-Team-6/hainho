package com.loopers.interfaces.consumer.ranking;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RankingCriticalKafkaConfig {
    public static final String RANKING_CRITICAL_LISTENER = "rankingCriticalListener";
    public static final String RANKING_CRITICAL_CONSUMER_FACTORY = "rankingCriticalConsumerFactory";
    public static final String RANKING_CRITICAL_GROUP = "ranking-critical-group";

    public static final int SESSION_TIMEOUT_MS = 60 * 1000; // session timeout = 1m
    public static final int HEARTBEAT_INTERVAL_MS = 20 * 1000; // heartbeat interval = 20s ( 1/3 of session_timeout )
    public static final int MAX_POLL_INTERVAL_MS = 2 * 60 * 1000; // max poll interval = 2m

    @Bean(RANKING_CRITICAL_CONSUMER_FACTORY)
    public ConsumerFactory<Object, Object> rankingConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = RANKING_CRITICAL_LISTENER)
    public ConcurrentKafkaListenerContainerFactory<Object, Object> rankingListenerContainerFactory(
            @Qualifier(RANKING_CRITICAL_CONSUMER_FACTORY) ConsumerFactory<Object, Object> rankingConsumerFactory,
            ByteArrayJsonMessageConverter converter
    ) {
        Map<String, Object> consumerConfig = new HashMap<>(rankingConsumerFactory.getConfigurationProperties());
        consumerConfig.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, SESSION_TIMEOUT_MS);
        consumerConfig.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, HEARTBEAT_INTERVAL_MS);
        consumerConfig.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, MAX_POLL_INTERVAL_MS);

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfig));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setRecordMessageConverter(converter);
        factory.setConcurrency(3);
        factory.setBatchListener(false);
        return factory;
    }
}
