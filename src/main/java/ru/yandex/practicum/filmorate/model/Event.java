package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Event {
    private final int eventId;
    private final int userId;
    private final long timestamp;
    private final String eventType;
    private final String operation;
    private final int entityId;

    public Map<String, Object> toMap() {
        Map<String, Object> event = new HashMap<>();
        event.put("user_id", userId);
        event.put("time", timestamp);
        event.put("event_type", eventType);
        event.put("operation", operation);
        event.put("entity_id", entityId);
        return event;
    }
}
