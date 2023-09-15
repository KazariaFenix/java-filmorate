package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import javax.validation.ValidationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Repository
@RequiredArgsConstructor
class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genre;
    private final MPAStorage mpa;
    private final DirectorDbStorage directorStorage;
    private final UserStorage userDbStorage;

    @Override
    public List<Film> getFilmList() {
        String sqlQuery = "SELECT * FROM films";
        Map<Integer, Set<FilmGenre>> genres = getAllGenre();
        Map<Integer, FilmMPA> mpa = getAllMpa();
        Map<Integer, Set<Director>> directors = getAllDirectors();
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genres, mpa, directors));
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
        addFilmDirectors(film, key);
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
        directorStorage.deleteFilmDirectors(film.getId());
        film = addFilmDirectors(film, film.getId());
        return findFilmById(film.getId());
    }

    @Override
    public Film findFilmById(int filmId) {
        validationFilm(filmId);
        String sqlFilm = "SELECT * FROM films WHERE film_id = ?";
        String sqlGenre = "SELECT genre_id FROM films_genre WHERE film_id = ?";
        String sqlUsersLike = "SELECT * FROM users_like WHERE film_id = ?";
        String sqlFilmDirectors = "SELECT * FROM films_directors WHERE film_id = ?";
        List<FilmGenre> genresFilm = jdbcTemplate.query(sqlGenre, (rs, rowNum) -> makeGenre(rs), filmId);
        List<Integer> usersLike = jdbcTemplate.query(sqlUsersLike, (rs, rowNum) -> makeUsersLike(rs), filmId);
        List<Director> filmsDirectors = jdbcTemplate.query(sqlFilmDirectors, (rs, rowNum) ->
                directorStorage.getDirectorById(rs.getInt("director_id")), filmId);
        SqlRowSet sqlQuery = jdbcTemplate.queryForRowSet(sqlFilm, filmId);
        Film film = null;
        if (sqlQuery.next()) {
            film = Film.builder()
                    .id(filmId)
                    .name(sqlQuery.getString("name"))
                    .rate(sqlQuery.getInt("rate"))
                    .description(sqlQuery.getString("description"))
                    .duration(sqlQuery.getInt("duration"))
                    .releaseDate(Objects.requireNonNull(sqlQuery.getDate("release_date")).toLocalDate())
                    .mpa(mpa.getMPAById(sqlQuery.getInt("mpa_id")))
                    .genres(new HashSet<>(genresFilm))
                    .userLike(usersLike)
                    .directors(filmsDirectors)
                    .build();
        }
        return film;
    }

    @Override
    public void putLike(int filmId, int userId) {
        checkExistUserId(userId);
        String sqlQuery = "INSERT INTO users_like(film_id, user_id) " +
                "SELECT ?,?  " +
                "WHERE NOT EXISTS (SELECT * FROM users_like WHERE film_id = ? AND user_id=?)";
        jdbcTemplate.update(sqlQuery, filmId, userId, filmId, userId);
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

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sqlQuery = "SELECT users_like.film_id FROM users_like " +
                "JOIN films ON films.film_id = users_like.film_id WHERE users_like.user_id = ? AND " +
                "users_like.film_id IN (SELECT film_id FROM users_like WHERE user_id = ?) " +
                "ORDER BY films.rate DESC";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> findFilmById(rs.getInt("film_id")),
                userId, friendId);
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
        if (film.getRate() >= 1) {
            jdbcTemplate.update(sqlRate, (film.getRate() - 1), filmId);
        }
    }

    @Override
    public List<Film> getPopularFilm(int count, int genreId, int year) {
        Map<Integer, Set<FilmGenre>> genres = getAllGenre();
        Map<Integer, FilmMPA> mpa = getAllMpa();
        Map<Integer, Set<Director>> directors = getAllDirectors();
        if (year == 0 && genreId != 0) {
            String sqlQuery = "SELECT f.* , COUNT(l.user_id) AS quantity " +
                    "FROM films AS f " +
                    "LEFT JOIN films_genre AS fl ON f.film_id = fl.film_id " +
                    "LEFT JOIN genre AS g ON fl.genre_id = g.genre_id " +
                    "LEFT JOIN users_like AS l ON f.film_id = l.film_id " +
                    "WHERE fl.genre_id = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY quantity DESC " +
                    "LIMIT ?";

            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genres, mpa, directors), genreId, count);
        }
        if (year != 0 && genreId == 0) {
            String sqlQuery = "SELECT f.*, COUNT(l.user_id) AS quantity " +
                    "FROM films f " +
                    "LEFT JOIN users_like AS l ON f.film_id = l.film_id " +
                    "WHERE EXTRACT(YEAR FROM CAST(release_date AS date)) = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY quantity DESC " +
                    "LIMIT ?";

            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genres, mpa, directors),
                    year, count);
        }
        if (year == 0) {
            String sqlQuery = "SELECT f.*, COUNT(l.user_id) AS quantity " +
                    "FROM FILMS AS f " +
                    "LEFT JOIN users_like AS l ON f.film_id = l.film_id " +
                    "GROUP BY f.film_id " +
                    "ORDER BY quantity DESC " +
                    "LIMIT ?";

            return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genres, mpa, directors), count);
        }
        String sqlQuery = "SELECT f.*, COUNT(l.user_id) AS quantity " +
                "FROM films AS f " +
                "LEFT JOIN films_genre AS fl ON f.film_id = fl.film_id " +
                "LEFT JOIN genre AS g ON fl.genre_id = g.genre_id " +
                "LEFT JOIN users_like AS l ON f.film_id = l.film_id " +
                "WHERE EXTRACT(YEAR FROM CAST(f.release_date AS date)) = ? " +
                "AND fl.genre_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY quantity DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, genres, mpa, directors),
                year, genreId, count);
    }

    @Override
    public void deleteFilm(int id) {
        validationFilm(id);
        String sqlUserLike = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlUserLike, id);
    }

    @Override
    public List<Film> searchFilms(String query, List<String> titleOrDirector) {
        String sql;
        String request = "%" + query + "%";
        boolean title = false;
        boolean director = false;
        Map<Integer, Set<FilmGenre>> filmIdGenres = getAllGenre();
        Map<Integer, FilmMPA> filmIdMpa = getAllMpa();
        Map<Integer, Set<Director>> filmIdDirector = getAllDirectors();

        for (String by : titleOrDirector) {
            if (by.equals("director")) {
                director = true;
            }
            if (by.equals("title")) {
                title = true;
            }
        }
        if (director && title) {
            sql = "SELECT f.film_id, " +
                    "       f.name, " +
                    "       f.description, " +
                    "       f.rate, " +
                    "       f.release_date, " +
                    "       f.duration, " +
                    "       f.mpa_id, " +
                    "       COUNT(ul.user_id) AS quantiy " +
                    "FROM FILMS f " +
                    "         LEFT JOIN users_like AS ul on f.film_id = ul.film_id " +
                    "         LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                    "         LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "WHERE f.name ILIKE ? " +
                    "   OR d.name ILIKE ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY quantiy desc;";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, filmIdGenres, filmIdMpa, filmIdDirector),
                    request, request);
        }
        if (title) {
            sql = "SELECT f.film_id, " +
                    "       f.name, " +
                    "       f.description, " +
                    "       f.rate, " +
                    "       f.release_date, " +
                    "       f.duration, " +
                    "       f.mpa_id, " +
                    "       COUNT(ul.user_id) AS quantiy " +
                    "FROM FILMS f " +
                    "         LEFT JOIN users_like AS ul on f.film_id = ul.film_id " +
                    "WHERE f.NAME ILIKE ? " +
                    "GROUP BY f.FILM_ID " +
                    "ORDER BY quantiy;";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, filmIdGenres, filmIdMpa, filmIdDirector),
                    request);
        }
        if (director) {
            sql = "SELECT f.film_id, " +
                    "       f.name, " +
                    "       f.description, " +
                    "       f.rate, " +
                    "       f.release_date, " +
                    "       f.duration, " +
                    "       f.mpa_id, " +
                    "       COUNT(ul.user_id) AS quantiy " +
                    "FROM FILMS f " +
                    "         LEFT JOIN users_like AS ul on f.film_id = ul.film_id " +
                    "         LEFT JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                    "         LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "WHERE d.name ILIKE ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY quantiy desc;";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, filmIdGenres, filmIdMpa, filmIdDirector),
                    request);
        } else throw new NoSuchElementException("Фильм с таким запросом не существует");
    }

    @Override
    public Collection<Film> filmsByDirectorSorted(int directorId, String sortBy) {
        directorStorage.getDirectorById(directorId);
        SqlRowSet sql;
        switch (sortBy) {
            case "year":
                sql = jdbcTemplate.queryForRowSet("SELECT f.* FROM films_directors AS fd " +
                        "JOIN FILMS AS f ON fd.film_id = f.film_id WHERE director_id = ? " +
                        "GROUP BY f.film_id, f.release_date ORDER BY f.release_date", directorId);
                break;

            case "likes":
                sql = jdbcTemplate.queryForRowSet("SELECT f.* FROM films_directors AS fd " +
                        "JOIN films AS f ON fd.film_id = f.film_id " +
                        "LEFT JOIN users_like AS ul ON f.film_id = ul.film_id WHERE director_id = ? " +
                        "GROUP BY f.film_id, ul.film_id IN (SELECT film_id FROM users_like) " +
                        "ORDER BY COUNT(ul.film_id) DESC", directorId);
                break;
            default:
                throw new ValidationException("Wrong 'sortBy' method");
        }
        Collection<Film> result = new ArrayList<>();
        while (sql.next()) {
            result.add(findFilmById(sql.getInt("film_id")));
        }
        return result;
    }

    private Film makeFilm(ResultSet rs, Map<Integer, Set<FilmGenre>> filmIdGenres, Map<Integer, FilmMPA> filmIdMpa,
                          Map<Integer, Set<Director>> filmIdDirector) throws SQLException {
        int filmId = rs.getInt("film_id");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(Objects.requireNonNull(rs.getTimestamp("release_date"))
                        .toLocalDateTime().toLocalDate()).duration(rs.getInt("duration"))
                .rate(rs.getInt("rate"))
                .mpa(filmIdMpa.get(filmId))
                .genres(filmIdGenres.getOrDefault(filmId, new HashSet<>()))
                .directors(filmIdDirector.getOrDefault(filmId, new HashSet<>()))
                .build();
    }

    private Map<Integer, Set<FilmGenre>> getAllGenre() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT fg.film_id, fg.genre_id , name FROM films_genre AS fg " +
                "INNER JOIN genre AS g on fg.genre_id = g.genre_id");

        Map<Integer, Set<FilmGenre>> filmsGenres = new HashMap<>();
        int filmId;
        while (rs.next()) {
            FilmGenre genre = FilmGenre.builder()
                    .id(rs.getInt("genre_id"))
                    .name(rs.getString("name"))
                    .build();
            Set<FilmGenre> genres = new HashSet<>();
            filmId = rs.getInt("film_id");
            if (filmsGenres.containsKey(filmId)) {
                genres = filmsGenres.get(filmId);
            }
            genres.add(genre);
            filmsGenres.put(filmId, genres);
        }
        return filmsGenres;
    }

    private Map<Integer, FilmMPA> getAllMpa() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT film_id, m.mpa_id, m.title FROM films " +
                "INNER JOIN mpa AS m ON films.mpa_id = m.mpa_id");
        Map<Integer, FilmMPA> filmIdMpa = new HashMap<>();
        while (rs.next()) {
            FilmMPA mpa = FilmMPA.builder()
                    .name(rs.getString("title"))
                    .id(rs.getInt("mpa_id"))
                    .build();
            filmIdMpa.put(rs.getInt("film_id"), mpa);
        }
        return filmIdMpa;
    }

    private Map<Integer, Set<Director>> getAllDirectors() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT film_id, d.director_id , name FROM  films_directors " +
                "LEFT JOIN directors AS d ON d.director_id = films_directors.director_id ORDER BY film_id");
        Map<Integer, Set<Director>> filmDirectors = new HashMap<>();
        int filmId;
        while (rs.next()) {
            Director director = Director.builder()
                    .id(rs.getInt("director_id"))
                    .name(rs.getString("name"))
                    .build();
            Set<Director> directors = new HashSet<>();
            filmId = rs.getInt("film_id");
            if (filmDirectors.containsKey(filmId)) {
                directors = filmDirectors.get(filmId);
            }
            directors.add(director);
            filmDirectors.put(rs.getInt("film_id"), directors);
        }
        return filmDirectors;
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

    private Film addFilmDirectors(Film film, int filmId) {
        directorStorage.setFilmsDirectors(film.getDirectors(), filmId);
        Collection<Director> directors = directorStorage.getFilmDirectorsSet(filmId);
        return film.toBuilder().directors(directors).build();
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

    private void checkExistUserId(long userId) {
        SqlRowSet sqlUser = jdbcTemplate.queryForRowSet("SELECT id FROM users WHERE id = ?", userId);
        if (!sqlUser.next()) {

            throw new NoSuchElementException(String.format("Пользователь с id: %d не найден", userId));
        }
    }

    private List<Integer> getUserFilmIds(int userId) {
        String sqlQuery = "SELECT film_id FROM users_like WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNUm) -> rs.getInt("film_id"), userId);
    }

    private List<Film> getUserFilms(int userId) {
        String sqlQuery = "SELECT film_id FROM users_like WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> findFilmById(rs.getInt("film_id")), userId);
    }

    private List<User> getOtherUserList(int userId) {
        String sqlQuery = "SELECT * FROM users WHERE id NOT IN (?)";

        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> userDbStorage.findUserById(rs.getLong("id")),
                userId);
    }
}
