package com.loopers.domain.audit;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audit_log")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class AuditLog extends BaseEntity {
    private String action;
    private String entity;
    private Long entityId;

    private AuditLog(String action, String entity, Long entityId) {
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
    }

    public static AuditLog create(String action, String entity, Long entityId) {
        return new AuditLog(action, entity, entityId);
    }
}
