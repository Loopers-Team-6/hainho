package com.loopers.interfaces.consumer.audit;

import com.loopers.application.audit.AuditLogFacade;
import com.loopers.confg.kafka.KafkaTopics;
import com.loopers.interfaces.consumer.KafkaMessage;
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
    public void consume(KafkaMessage<?> message, Acknowledgment acknowledgment) {
        switch (message.eventType()) {
            case "OrderCreated" -> {
                OrderCreated value = (OrderCreated) message.payload();
                auditLogFacade.logEvent(value, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);
            }
            case "OrderCancelled" -> {
                OrderCancelled value = (OrderCancelled) message.payload();
                auditLogFacade.logEvent(value, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);
            }
            case "OrderCompleted" -> {
                OrderCompleted value = (OrderCompleted) message.payload();
                auditLogFacade.logEvent(value, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);
            }
            case "PaymentSucceed" -> {
                PaymentSucceed value = (PaymentSucceed) message.payload();
                auditLogFacade.logEvent(value, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);
            }
            case "PaymentFailed" -> {
                PaymentFailed value = (PaymentFailed) message.payload();
                auditLogFacade.logEvent(value, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);
            }
            case "CardPaymentCreated" -> {
                CardPaymentCreated value = (CardPaymentCreated) message.payload();
                auditLogFacade.logEvent(value, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);
            }
            case "PointPaymentCreated" -> {
                PointPaymentCreated value = (PointPaymentCreated) message.payload();
                auditLogFacade.logEvent(value, message.eventId(), AuditLogKafkaConfig.AUDIT_LOG_GROUP);
            }
            default -> {
                // 알 수 없는 이벤트 타입 처리 (로깅 등)
            }
        }
        acknowledgment.acknowledge();
    }
}
