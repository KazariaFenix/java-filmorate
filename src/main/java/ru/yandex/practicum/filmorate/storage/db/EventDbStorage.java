package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Primary
public class EventDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addEvent(int entityId, int userId, String eventType, String operation) {
        Event event = Event.builder()
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("event_id");
        int key = simpleJdbcInsert.executeAndReturnKey(event.toMap()).intValue();
    }

    public List<Event> getAllEventByUserId(int userId) {
        String sql = "SELECT * FROM events WHERE user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), userId);
    }

    public Event makeEvent(ResultSet rs) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("event_id"))
                .userId(rs.getInt("user_id"))
                .timestamp(rs.getTimestamp("time").toLocalDateTime())
                .entityId(rs.getInt("entity_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .build();
    }
}
