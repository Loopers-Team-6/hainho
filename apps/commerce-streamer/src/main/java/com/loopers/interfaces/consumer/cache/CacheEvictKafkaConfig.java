package com.loopers.interfaces.consumer.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.confg.kafka.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheEvictKafkaConfig {
    public static final String CACHE_EVICT_LISTENER = "cacheEvictListener";
    public static final String CACHE_EVICT_CONSUMER_FACTORY = "cacheEvictConsumerFactory";

    @Bean(CACHE_EVICT_CONSUMER_FACTORY)
    public ConsumerFactory<Object, Object> cacheEvictConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = CACHE_EVICT_LISTENER)
    public ConcurrentKafkaListenerContainerFactory<Object, Object> cacheEvictListenerContainerFactory(
            @Qualifier(CACHE_EVICT_CONSUMER_FACTORY) ConsumerFactory<Object, Object> cacheEvictConsumerFactory,
            ObjectMapper objectMapper
    ) {
        Map<String, Object> consumerConfig = new HashMap<>(cacheEvictConsumerFactory.getConfigurationProperties());
        consumerConfig.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, KafkaConfig.MAX_POLLING_SIZE);
        consumerConfig.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, KafkaConfig.FETCH_MIN_BYTES);
        consumerConfig.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, KafkaConfig.FETCH_MAX_WAIT_MS);
        consumerConfig.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, KafkaConfig.SESSION_TIMEOUT_MS);
        consumerConfig.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, KafkaConfig.HEARTBEAT_INTERVAL_MS);
        consumerConfig.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, KafkaConfig.MAX_POLL_INTERVAL_MS);

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfig));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setBatchMessageConverter(new BatchMessagingMessageConverter(new ByteArrayJsonMessageConverter(objectMapper)));
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        return factory;
    }
}