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
        Map<String, Object> user = new HashMap<>();

        user.put("user_id", userId);
        user.put("time", timestamp);
        user.put("event_type", eventType);
        user.put("operation", operation);
        user.put("entity_id", entityId);

        return user;
    }
}
