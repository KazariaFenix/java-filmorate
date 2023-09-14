package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventStatus;
import ru.yandex.practicum.filmorate.model.EventType;

import java.util.List;

public interface EventStorage {
    void addEvent(int entityId, int userId, EventType eventType, EventStatus operation);

    List<Event> getAllEventByUserId(int userId);
}
