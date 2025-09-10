package com.loopers.interfaces.consumer.audit;

import com.loopers.application.audit.AuditLogFacade;
import com.loopers.confg.kafka.KafkaTopics;
import com.loopers.interfaces.consumer.events.order.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLogConsumer {
    private final AuditLogFacade auditLogFacade;

    @KafkaListener(
            topics = {KafkaTopics.ORDER},
            groupId = AuditLogKafkaConfig.AUDIT_LOG_GROUP,
            containerFactory = AuditLogKafkaConfig.AUDIT_LOG_LISTENER
    )
    public void consumeOrderTopicEvent(OrderTopicMessage message, Acknowledgment acknowledgment) {
        switch (message.payload()) {
            case OrderCreated event ->
                    auditLogFacade.logEvent(event, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);

            case OrderCompleted event ->
                    auditLogFacade.logEvent(event, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);

            case OrderCancelled event ->
                    auditLogFacade.logEvent(event, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);

            case PaymentSucceed event ->
                    auditLogFacade.logEvent(event, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);

            case PaymentFailed event ->
                    auditLogFacade.logEvent(event, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);

            case CardPaymentCreated event ->
                    auditLogFacade.logEvent(event, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);

            case PointPaymentCreated event ->
                    auditLogFacade.logEvent(event, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);
        }
        acknowledgment.acknowledge();
    }
}
