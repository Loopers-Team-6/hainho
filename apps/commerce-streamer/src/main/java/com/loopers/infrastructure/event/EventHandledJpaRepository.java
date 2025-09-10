package com.loopers.infrastructure.event;

import com.loopers.domain.event.EventHandled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EventHandledJpaRepository extends JpaRepository<EventHandled, Long> {
    @Modifying
    @Query(value = """
            INSERT INTO event_handled(event_id, group_id, created_at, updated_at)
            VALUES (:eventId, :groupId, NOW(), NOW())
            ON DUPLICATE KEY UPDATE group_id = group_id
            """, nativeQuery = true)
    int insertOrIgnoreDuplicate(String eventId, String groupId);
}
