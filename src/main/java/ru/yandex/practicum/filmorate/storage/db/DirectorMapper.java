package ru.yandex.practicum.filmorate.storage.db;

import io.micrometer.core.annotation.Timed;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

class DirectorMapper implements RowMapper<Director> {
    @Override
    @Loggable
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }
}
