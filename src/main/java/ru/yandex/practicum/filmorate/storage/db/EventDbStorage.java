package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventStatus;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Repository
@Primary
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int addEvent(int entityId, int userId, EventType eventType, EventStatus operation) {
        Event event = Event.builder()
                .eventType(eventType.name())
                .operation(operation.name())
                .entityId(entityId)
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("event_id");
        return simpleJdbcInsert.executeAndReturnKey(event.toMap()).intValue();
    }

    @Override
    public List<Event> getAllEventByUserId(int userId) {
        String sql = "SELECT * FROM events WHERE user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), userId);
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("event_id"))
                .userId(rs.getInt("user_id"))
                .timestamp(rs.getLong("time"))
                .entityId(rs.getInt("entity_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .build();
    }
}
