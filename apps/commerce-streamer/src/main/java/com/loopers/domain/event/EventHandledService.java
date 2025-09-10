package com.loopers.domain.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class EventHandledService {
    private final EventHandledRepository eventHandledRepository;

    @Transactional
    public boolean markHandledIfAbsent(String eventId, String groupId) {
        int inserted = eventHandledRepository.insertOrIgnoreDuplicate(eventId, groupId);
        return isSuccess(inserted);
    }

    private boolean isSuccess(int inserted) {
        return inserted == 1;
    }
}
