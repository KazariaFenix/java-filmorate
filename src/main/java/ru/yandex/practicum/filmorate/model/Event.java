package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Event {
    private final int eventId;
    @Positive
    private final int userId;
    @Positive
    private final long timestamp;
    @NotBlank
    private final String eventType;
    @NotBlank
    private final String operation;
    @Positive
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
