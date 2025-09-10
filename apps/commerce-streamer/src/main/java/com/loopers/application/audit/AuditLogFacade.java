package com.loopers.application.audit;

import com.loopers.domain.audit.AuditLogService;
import com.loopers.domain.event.EventHandledService;
import com.loopers.interfaces.consumer.events.order.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLogFacade {
    private final AuditLogService auditLogService;
    private final EventHandledService eventHandledService;

    @Transactional
    public void logEvent(OrderCreated event, String eventId, String groupId) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        auditLogService.logEvent(event);
    }

    @Transactional
    public void logEvent(OrderCancelled event, String eventId, String groupId) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        auditLogService.logEvent(event);
    }

    @Transactional
    public void logEvent(OrderCompleted event, String eventId, String groupId) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        auditLogService.logEvent(event);
    }

    @Transactional
    public void logEvent(PaymentSucceed event, String eventId, String groupId) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        auditLogService.logEvent(event);
    }

    @Transactional
    public void logEvent(PaymentFailed event, String eventId, String groupId) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        auditLogService.logEvent(event);
    }

    @Transactional
    public void logEvent(CardPaymentCreated event, String eventId, String groupId) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        auditLogService.logEvent(event);
    }

    @Transactional
    public void logEvent(PointPaymentCreated event, String eventId, String groupId) {
        if (!eventHandledService.markHandledIfAbsent(eventId, groupId)) {
            return;
        }
        auditLogService.logEvent(event);
    }
}