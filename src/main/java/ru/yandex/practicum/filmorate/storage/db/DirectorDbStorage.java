package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
public class DirectorDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Director addDirector(Director director) {
        String sqlQuery = "INSERT into directors(name) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        log.info("Director {} was successfully added", director.getName());
        return director;
    }

    public Director editDirector (Director director) {
        String query = "UPDATE directors SET name = ? WHERE director_id = ?";
        int countLines = jdbcTemplate.update(query,
                director.getName(),
                director.getId());
        if (countLines == 0) {
            log.warn("Director id={} was not found", director.getId());
            throw new NoSuchElementException(
                    String.format("Director '%s' id=%s was not found", director.getName(), director.getId()));
        }
        log.info("Director id={} was updated", director.getId());
        return director;
    }

    public Director getDirectorById (int id) {
        Director director;
        try {
            director = jdbcTemplate.queryForObject(
                    "SELECT * FROM directors WHERE director_id = ?", new DirectorMapper(), id);
            log.debug("Director id={} found", director.getId());
            return director;
        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchElementException(String.format("Director id=%s was not found", id));
        }
    }

    public Collection<Director> getAllDirectors() {
        Collection<Director> directors = jdbcTemplate.query("SELECT * FROM directors", new DirectorMapper());
        return directors;
    }

    public boolean killDirectorById (int id) {
        int countDirectorsTable = jdbcTemplate.update("DELETE FROM directors WHERE director_id = ?", id);
        int countConnectionTable = jdbcTemplate.update("DELETE FROM films_directors WEHRE directir_id = ?", id);
        if (countConnectionTable > 0 && countDirectorsTable > 0) return true;
        else {
            log.debug(
                    "Couldn't delete director id={}. Delete status from directors table is {}. " +
                            "Delete status for connection table is {}", id, countDirectorsTable, countConnectionTable);
            return false;
        }
    }
}

class DirectorMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }
}
