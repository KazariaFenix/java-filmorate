package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Component
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // main methods
    @Override
    public Director addDirector(Director director) {
        String sqlQuery = "INSERT INTO directors(name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        log.info("Director {} was successfully added", director.getName());
        return director;
    }

    @Override
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

    @Override
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

    @Override
    public Collection<Director> getAllDirectors() {
        Collection<Director> directors = jdbcTemplate.query("SELECT * FROM directors", new DirectorMapper());
        return directors;
    }

    @Override
    public boolean killDirectorById (int id) {
        String sqlQuery = "delete from directors " +
                "where director_id = ?";
        int status = jdbcTemplate.update(sqlQuery, id);
        return status != 0;
    }

    // service methods for directors - films link table
    @Override
    public void setFilmsDirectors(Collection<Director> directors, int filmId) {
        if (directors == null || directors.size() == 0 || directors.isEmpty()) return;
        String query = "INSERT INTO films_directors(film_id, director_id) VALUES(?,?)";
        for (Director director : directors) {
            try {
                director = jdbcTemplate.queryForObject("SELECT * FROM directors WHERE director_id = ?",
                        new DirectorMapper(), director.getId());
                jdbcTemplate.update(query, filmId, director.getId());
            } catch (DataIntegrityViolationException | NullPointerException ex) {
                throw new RuntimeException(String.format("Couldn't update directors list for film id=%s", filmId));
            }
        }
    }

    @Override
    public Collection<Director> getFilmDirectorsSet(int filmId) {
        String query =
                "SELECT d.* " +
                "FROM films_directors fd " +
                "JOIN directors d on d.director_id = fd.director_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(query, new DirectorMapper(), filmId);
    }

    @Override
    public void deleteFilmDirectors(int filmId) {
        String query = "delete from films_directors " +
                "where film_id = ?";
        jdbcTemplate.update(query, filmId);
    }

}

// service class
class DirectorMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("name"))
                .build();
    }
}
