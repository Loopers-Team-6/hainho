package com.loopers.interfaces.consumer.audit;

import com.loopers.application.audit.AuditLogFacade;
import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.interfaces.consumer.KafkaMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLogConsumer {
    private final AuditLogFacade auditLogFacade;

    @Value("${kafka.group.audit-log}")
    private String groupId;

    @KafkaListener(
            topics = {"${kafka.topic.order}"},
            groupId = "${kafka.group.audit-log}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consume(List<KafkaMessage<?>> messages, Acknowledgment acknowledgment) {
        for (KafkaMessage<?> message : messages) {
            switch (message.eventType()) {
                case "OrderCreated" -> {
                    OrderCreated value = (OrderCreated) message.payload();
                    auditLogFacade.logEvent(value, message.eventId(), groupId);
                }
                case "OrderCancelled" -> {
                    OrderCancelled value = (OrderCancelled) message.payload();
                    auditLogFacade.logEvent(value, message.eventId(), groupId);
                }
                case "OrderCompleted" -> {
                    OrderCompleted value = (OrderCompleted) message.payload();
                    auditLogFacade.logEvent(value, message.eventId(), groupId);
                }
                case "PaymentSucceed" -> {
                    PaymentSucceed value = (PaymentSucceed) message.payload();
                    auditLogFacade.logEvent(value, message.eventId(), groupId);
                }
                case "PaymentFailed" -> {
                    PaymentFailed value = (PaymentFailed) message.payload();
                    auditLogFacade.logEvent(value, message.eventId(), groupId);
                }
                case "CardPaymentCreated" -> {
                    CardPaymentCreated value = (CardPaymentCreated) message.payload();
                    auditLogFacade.logEvent(value, message.eventId(), groupId);
                }
                case "PointPaymentCreated" -> {
                    PointPaymentCreated value = (PointPaymentCreated) message.payload();
                    auditLogFacade.logEvent(value, message.eventId(), groupId);
                }
                default -> {
                    // 알 수 없는 이벤트 타입 처리 (로깅 등)
                }
            }
        }
        acknowledgment.acknowledge();
    }
}
