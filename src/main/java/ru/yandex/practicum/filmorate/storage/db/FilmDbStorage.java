package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genre;
    private final MPAStorage mpa;
    private final UserDbStorage userDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genre, MPAStorage mpa, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genre = genre;
        this.mpa = mpa;
        this.userDbStorage = userDbStorage;
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
        } else {
            deleteGenre(film);
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

    @Override
    public void putLike(int filmId, int userId) {
        if (validationUserLike(filmId, userId)) {
            throw new NoSuchElementException("Данный пользователь уже поставил лайк этому фильму");
        }
        String sqlUserLike = "MERGE INTO users_like (film_id, user_id) VALUES (?, ?)";
        String sqlRate = "UPDATE films SET rate = ? WHERE film_id = ?";
        Film film = findFilmById(filmId);

        jdbcTemplate.update(sqlUserLike, filmId, userId);
        jdbcTemplate.update(sqlRate, (film.getRate() + 1), filmId);
    }

    @Override
    public List<Film> getRecommendedFilms(int userId) {

        List<Integer> userFilmIds = getUserFilmIds(userId);
        List<User> otherUsers = getOtherUserList(userId);

        List<List<Film>> collect = otherUsers.stream()
                .map(user -> getUserFilms((int) user.getId()))
                .collect(Collectors.toList());

        List<Film> targetFilms = new ArrayList<>();
        int targetAmount = 0;
        int currentAmount = 0;

        for (List<Film> films : collect) {
            if (films == null || films.isEmpty()) {
                continue;
            }
            for (Film film : films) {
                if (userFilmIds.contains(film.getId())) {
                    currentAmount++;
                }
            }
            if (currentAmount > targetAmount) {
                targetFilms = films;
                targetAmount = currentAmount;
            }
            currentAmount = 0;
        }

        if (targetFilms.isEmpty()) {
            return targetFilms;
        }

        return targetFilms.stream()
                .filter(film -> !userFilmIds.contains(film.getId()))
                .collect(Collectors.toList());
    }

    private List<Integer> getUserFilmIds(int userId) {
        String sqlQuery = "SELECT film_id FROM users_like WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNUm) -> rs.getInt("film_id"), userId);
    }

    private List<Film> getUserFilms(int userId) {
        String sqlQuery = "SELECT film_id FROM films WHERE film_id IN " +
                "(SELECT film_id FROM users_like WHERE user_id = ?) ORDER BY film_id";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> findFilmById(rs.getInt("film_id")), userId);
    }

    private List<User> getOtherUserList(int userId) {
        String sqlQuery = "SELECT * FROM users where id not in (?)";

        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> userDbStorage.findUserById(rs.getLong("id")),
                userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        if (!validationUserLike(filmId, userId)) {
            throw new NoSuchElementException("Данный пользователь не ставил лайк этому фильму");
        }
        String sqlUserLike = "DELETE FROM users_like WHERE film_id = ? AND user_id = ?";
        String sqlRate = "UPDATE films SET rate = ? WHERE film_id = ?";
        Film film = findFilmById(filmId);

        jdbcTemplate.update(sqlUserLike, filmId, userId);
        jdbcTemplate.update(sqlRate, (film.getRate() - 1), filmId);
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        String sqlQuery = "SELECT * FROM films ORDER BY rate DESC LIMIT ?;";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> findFilmById(rs.getInt("film_id")),
                count);
    }

    private FilmGenre makeGenre(ResultSet rs) throws SQLException {
        return genre.getGenreById(rs.getInt("genre_id"));
    }

    private Integer makeUsersLike(ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }

    private void updateGenre(Film film) {
        deleteGenre(film);
        addFilmGenres(film, film.getId());
    }

    private void deleteGenre(Film film) {
        String sqlDelete = "DELETE FROM films_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlDelete, film.getId());
    }

    private void addFilmGenres(Film film, long key) {
        Set<FilmGenre> filmGenres = new HashSet<>(film.getGenres());

        for (FilmGenre genre : filmGenres) {
            String sqlFilmGenres = "MERGE INTO films_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlFilmGenres, key, genre.getId());
        }
    }

    private boolean validationUserLike(int filmId, int userId) {
        String sqlQuery = "SELECT * FROM users_like WHERE film_id =? AND user_id = ?";
        SqlRowSet userLike = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);

        return userLike.next();
    }

    private void validationFilm(long filmId) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet film = jdbcTemplate.queryForRowSet(sqlQuery, filmId);

        if (!film.next()) {
            throw new NoSuchElementException("Фильма с таким идентификатором не существует");
        }
    }
}
