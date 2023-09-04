package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MPADao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreImpl genre;
    private final MPADao mpa;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreImpl genre, MPADao mpa) {
        this.jdbcTemplate = jdbcTemplate;
        this.genre = genre;
        this.mpa = mpa;
    }

    private void validationFilm(long filmId) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet film = jdbcTemplate.queryForRowSet(sqlQuery, filmId);

        if (!film.next()) {
            throw new NoSuchElementException("Фильма с таким идентификатором не существует");
        }
    }

    @Override
    public List<Film> getFilmList() {
        String sqlQuery = "SELECT film_id FROM films ORDER BY film_id";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> findFilmById(rs.getInt("film_id")));
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        int key = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();

        if (film.getGenres() != null) {
            addFilmGenres(film, key);
        }
        return findFilmById(key);
    }

    @Override
    public Film updateFilm(Film film) {
        validationFilm(film.getId());

        String sqlQuery = "UPDATE films SET name = ?, rate = ?, description = ?,  duration = ?, release_date = ?, " +
                "mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getRate(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            updateGenre(film);
        }
        return findFilmById(film.getId());
    }

    @Override
    public Film findFilmById(int filmId) {
        validationFilm(filmId);

        String sqlFilm = "SELECT * FROM films WHERE film_id = ?";
        String sqlGenre = "SELECT genre_id FROM films_genre WHERE film_id = ?";
        String sqlUsersLike = "SELECT * FROM users_like WHERE film_id = ?";
        List<FilmGenre> genresFilm = jdbcTemplate.query(sqlGenre, (rs, rowNum) -> makeGenre(rs), filmId);
        List<Integer> usersLike = jdbcTemplate.query(sqlUsersLike, (rs, rowNum) -> makeUsersLike(rs), filmId);
        SqlRowSet sqlQuery = jdbcTemplate.queryForRowSet(sqlFilm, filmId);
        Film film = null;

        if (sqlQuery.next()) {
            film = Film.builder()
                    .id(filmId)
                    .name(sqlQuery.getString("name"))
                    .rate(sqlQuery.getInt("rate"))
                    .description(sqlQuery.getString("description"))
                    .duration(sqlQuery.getInt("duration"))
                    .releaseDate(sqlQuery.getDate("release_date").toLocalDate())
                    .mpa(mpa.getMPAById(sqlQuery.getInt("mpa_id")))
                    .genres(new HashSet<>(genresFilm))
                    .userLike(usersLike)
                    .build();
        }
        return film;
    }

    private FilmGenre makeGenre(ResultSet rs) throws SQLException {
        return genre.getGenreById(rs.getInt("genre_id"));
    }

    private Integer makeUsersLike(ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }

    private void updateGenre(Film film) {
        String sqlDelete = "DELETE FROM films_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlDelete, film.getId());
        addFilmGenres(film, film.getId());
    }

    private void addFilmGenres(Film film, long key) {
        Set<FilmGenre> filmGenres = new HashSet<>(film.getGenres());

        for (FilmGenre genre : filmGenres) {
            String sqlFilmGenres = "MERGE INTO films_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlFilmGenres, key, genre.getId());
        }
    }
}
