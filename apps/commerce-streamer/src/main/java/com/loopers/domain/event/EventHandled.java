package com.loopers.domain.event;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "event_handled",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_handled_event_id_group_id",
                        columnNames = {"eventId", "groupId"}
                )
        }
)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class EventHandled extends BaseEntity {
    private String eventId;
    private String groupId;

    private EventHandled(String eventId, String groupId) {
        this.eventId = eventId;
        this.groupId = groupId;
    }

    public static EventHandled create(String eventId, String groupId) {
        return new EventHandled(eventId, groupId);
    }
}
