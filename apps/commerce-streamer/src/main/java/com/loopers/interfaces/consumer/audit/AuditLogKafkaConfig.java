package com.loopers.interfaces.consumer.audit;

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
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AuditLogKafkaConfig {
    public static final String AUDIT_LOG_LISTENER = "auditLogListener";
    public static final String AUDIT_LOG_CONSUMER_FACTORY = "auditLogConsumerFactory";
    public static final String AUDIT_LOG_GROUP = "audit-log-group";

    @Bean(AUDIT_LOG_CONSUMER_FACTORY)
    public ConsumerFactory<Object, Object> auditLogConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = AUDIT_LOG_LISTENER)
    public ConcurrentKafkaListenerContainerFactory<Object, Object> auditLogListenerContainerFactory(
            @Qualifier(AUDIT_LOG_CONSUMER_FACTORY) ConsumerFactory<Object, Object> auditLogConsumerFactory,
            ObjectMapper objectMapper
    ) {
        Map<String, Object> consumerConfig = new HashMap<>(auditLogConsumerFactory.getConfigurationProperties());
        consumerConfig.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, KafkaConfig.SESSION_TIMEOUT_MS);
        consumerConfig.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, KafkaConfig.HEARTBEAT_INTERVAL_MS);
        consumerConfig.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, KafkaConfig.MAX_POLL_INTERVAL_MS);

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfig));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setRecordMessageConverter(new ByteArrayJsonMessageConverter(objectMapper));
        factory.setConcurrency(3);
        factory.setBatchListener(false);
        return factory;
    }
}