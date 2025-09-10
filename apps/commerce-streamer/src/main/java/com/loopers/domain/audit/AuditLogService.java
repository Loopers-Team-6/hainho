package com.loopers.domain.audit;

import com.loopers.interfaces.consumer.events.order.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public void logEvent(OrderCreated event) {
        AuditLog auditLog = AuditLog.create(event.getClass().getName(), "Order", event.orderId());
        auditLogRepository.save(auditLog);
    }

    public void logEvent(OrderCancelled event) {
        AuditLog auditLog = AuditLog.create(event.getClass().getName(), "Order", event.orderId());
        auditLogRepository.save(auditLog);
    }

    public void logEvent(OrderCompleted event) {
        AuditLog auditLog = AuditLog.create(event.getClass().getName(), "Order", event.orderId());
        auditLogRepository.save(auditLog);
    }

    public void logEvent(PaymentSucceed event) {
        AuditLog auditLog = AuditLog.create(event.getClass().getName(), "Payment", event.orderId());
        auditLogRepository.save(auditLog);
    }

    public void logEvent(PaymentFailed event) {
        AuditLog auditLog = AuditLog.create(event.getClass().getName(), "Payment", event.orderId());
        auditLogRepository.save(auditLog);
    }

    public void logEvent(CardPaymentCreated event) {
        AuditLog auditLog = AuditLog.create(event.getClass().getName(), "Payment", event.orderId());
        auditLogRepository.save(auditLog);
    }

    public void logEvent(PointPaymentCreated event) {
        AuditLog auditLog = AuditLog.create(event.getClass().getName(), "Payment", event.orderId());
        auditLogRepository.save(auditLog);
    }
}
