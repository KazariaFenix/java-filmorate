package ru.yandex.practicum.filmorate.storage.db;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;

import java.util.Collection;
import java.util.Objects;

@Component
@RequiredArgsConstructor
class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

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
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return director;
    }

    @Override
    @Loggable
    public Director editDirector(Director director) {
        String query = "UPDATE directors SET name = ? WHERE director_id = ?";
        int countLines = jdbcTemplate.update(query,
                director.getName(),
                director.getId());
        if (countLines == 0) {
            throw new NoSuchElementException(
                    String.format("Director '%s' id=%s was not found", director.getName(), director.getId()));
        }
        return director;
    }

    @Override
    @Loggable
    public Director getDirectorById(int id) {
        Director director;
        try {
            director = jdbcTemplate.queryForObject(
                    "SELECT * FROM directors WHERE director_id = ?", new DirectorMapper(), id);
            return director;
        } catch (EmptyResultDataAccessException ex) {
            throw new NoSuchElementException(String.format("Director id=%s was not found", id));
        }
    }

    @Override
    @Loggable
    public Collection<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM directors", new DirectorMapper());
    }

    @Override
    @Loggable
    public boolean deleteDirectorById(int id) {
        String sqlQuery = "DELETE FROM directors " +
                "WHERE director_id = ?";
        int status = jdbcTemplate.update(sqlQuery, id);
        return status != 0;
    }

    // service methods for directors - films link table
    @Override
    @Loggable
    public void setFilmsDirectors(Collection<Director> directors, int filmId) {
        if (directors == null || directors.isEmpty()) {
            return;
        }
        String query = "INSERT INTO films_directors(film_id, director_id) VALUES(?,?)";
        for (Director director : directors) {
            try {
                director = jdbcTemplate.queryForObject("SELECT * FROM directors WHERE director_id = ?",
                        new DirectorMapper(), director.getId());
                jdbcTemplate.update(query, filmId, Objects.requireNonNull(director).getId());
            } catch (DataIntegrityViolationException | NullPointerException ex) {
                throw new RuntimeException(String.format("Couldn't update directors list for film id=%s", filmId));
            }
        }
    }

    @Override
    @Loggable
    public Collection<Director> getFilmDirectorsSet(int filmId) {
        String query =
                "SELECT d.* " +
                        "FROM films_directors fd " +
                        "JOIN directors d on d.director_id = fd.director_id " +
                        "WHERE film_id = ?";
        return jdbcTemplate.query(query, new DirectorMapper(), filmId);
    }

    @Override
    @Loggable
    public void deleteFilmDirectors(int filmId) {
        String query = "delete from films_directors " +
                "where film_id = ?";
        jdbcTemplate.update(query, filmId);
    }
}

