package ru.yandex.practicum.filmorate.service.db;

import lombok.Getter;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceInt;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@Primary
public class FilmServiceDb implements FilmServiceInt {
    private final JdbcTemplate jdbcTemplate;
    @Getter
    private final FilmStorage filmStorage;

    public FilmServiceDb(JdbcTemplate jdbcTemplate, FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    @Override
    public void putLike(int filmId, int userId) {
        if (validationUserLike(filmId, userId)) {
            throw new NoSuchElementException("Данный пользователь уже поставил лайк этому фильму");
        }
        String sqlUserLike = "MERGE INTO users_like (film_id, user_id) VALUES (?, ?)";
        String sqlRate = "UPDATE films SET rate = ? WHERE film_id = ?";
        Film film = filmStorage.findFilmById(filmId);

        jdbcTemplate.update(sqlUserLike, filmId, userId);
        jdbcTemplate.update(sqlRate, (film.getRate() + 1), filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        if (!validationUserLike(filmId, userId)) {
            throw new NoSuchElementException("Данный пользователь не ставил лайк этому фильму");
        }
        String sqlUserLike = "DELETE FROM users_like WHERE film_id = ? AND user_id = ?";
        String sqlRate = "UPDATE films SET rate = ? WHERE film_id = ?";
        Film film = filmStorage.findFilmById(filmId);

        jdbcTemplate.update(sqlUserLike, filmId, userId);
        jdbcTemplate.update(sqlRate, (film.getRate() - 1), filmId);
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        String sqlQuery = "SELECT * FROM films ORDER BY rate DESC LIMIT ?;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> filmStorage.findFilmById(rs.getInt("film_id")),
                count);
    }

    private boolean validationUserLike(int filmId, int userId) {
        String sqlQuery = "SELECT * FROM users_like WHERE film_id =? AND user_id = ?";
        SqlRowSet userLike = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);

        if (userLike.next()) {
            return true;
        } else {
            return false;
        }
    }
}
