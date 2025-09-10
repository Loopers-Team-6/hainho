package com.loopers.domain.event;

public interface EventHandledRepository {
    EventHandled save(EventHandled eventHandled);

    int insertOrIgnoreDuplicate(String eventId, String groupId);
}
