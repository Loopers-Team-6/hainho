package com.loopers.domain.audit;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
}
