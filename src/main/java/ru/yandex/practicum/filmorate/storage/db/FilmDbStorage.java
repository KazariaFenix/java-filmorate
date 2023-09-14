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
    private final GenreStorage genre;
    private final MPAStorage mpa;
    private final DirectorStorage directorStorage;
    private final UserStorage userDbStorage;

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
        addFilmDirectors(film, key);
        film.setId(key);
        return film;
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
                    .releaseDate((Objects.requireNonNull(sqlQuery.getDate("release_date"))).toLocalDate())
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
        String sqlQuery = "SELECT film_id FROM users_like WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> findFilmById(rs.getInt("film_id")), userId);
    }

    private List<User> getOtherUserList(int userId) {
        String sqlQuery = "SELECT * FROM users where id not in (?)";

        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> userDbStorage.findUserById(rs.getLong("id")),
                userId);
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sqlQuery = "SELECT users_like.film_id FROM users_like JOIN films ON films.film_id = users_like.film_id " +
                "WHERE users_like.user_id = ? AND " +
                "users_like.film_id IN (SELECT film_id FROM users_like WHERE user_id = ?) " +
                "ORDER BY films.rate DESC";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> findFilmById(rs.getInt("film_id")), userId, friendId);
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
    public List<Film> getPopularFilm(int count, int genreId, int year) {
        String sqlQuery = "select *\n" +
                "from FILMS  F LEFT JOIN  users_like L on F.FILM_ID  = L.FILM_ID\n " +
                "GROUP BY F.FILM_ID, L.USER_ID ORDER BY COUNT(L.USER_ID) DESC LIMIT ?";

        Map<Long, Set<FilmGenre>> filmIdGenres = getAllGenre();
        Map<Long, FilmMPA> filmIdMpa = getAllMpa();
        Map<Long, Set<Director>> filmIdDirector = getAllDirectors();
        List<Film> list = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs, filmIdGenres, filmIdMpa, filmIdDirector),
                count);
        Set<Film> filmSet = new HashSet<>(list);
        list.clear();
        list.addAll(filmSet);
        return list;
    }

    @Override
    public void deleteFilm(int id) {
        validationFilm(id);
        String sqlUserLike = "DELETE FROM FILMS WHERE film_id = ?";
        jdbcTemplate.update(sqlUserLike, id);
    }

    @Override
    public List<Film> searchFilms(String query, List<String> titleOrDirector) {
        String sql;

        String request = "%" + query + "%";
        boolean title = false;
        boolean director = false;

        Map<Long, Set<FilmGenre>> filmIdGenres = getAllGenre();
        Map<Long, FilmMPA> filmIdMpa = getAllMpa();
        Map<Long, Set<Director>> filmIdDirector = getAllDirectors();

        for (String by : titleOrDirector) {
            if (by.equals("director")) {
                director = true;
            }
            if (by.equals("title")) {
                title = true;
            }
        }

        if (director && title) {
            sql = "SELECT f.* ," +
                    "       COUNT(ul.user_id) as quantiy\n" +
                    "FROM FILMS f\n" +
                    "         LEFT JOIN users_like ul on f.film_id = ul.film_id\n" +
                    "         LEFT JOIN films_directors fd ON f.film_id = fd.film_id\n" +
                    "         LEFT JOIN directors d ON fd.director_id = d.director_id\n" +
                    "WHERE f.name ILIKE ?\n" +
                    "   OR d.name ILIKE ?\n" +
                    "group by f.FILM_ID\n" +
                    "ORDER BY quantiy desc;";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, filmIdGenres, filmIdMpa, filmIdDirector), request, request);
        }
        if (title) {
            sql = "SELECT f.* ," +
                    "       COUNT(ul.user_id) as quantiy\n" +
                    "FROM FILMS f\n" +
                    "         LEFT JOIN users_like ul on f.film_id = ul.film_id\n" +
                    "WHERE f.NAME ilike ?\n" +
                    "group by f.FILM_ID\n" +
                    "ORDER BY quantiy;";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, filmIdGenres, filmIdMpa, filmIdDirector), request);
        }
        if (director) {
            sql = "SELECT f.* ," +
                    "       COUNT(ul.user_id) as quantiy\n" +
                    "FROM FILMS f\n" +
                    "         LEFT JOIN users_like ul on f.film_id = ul.film_id\n" +
                    "         LEFT JOIN films_directors fd ON f.film_id = fd.film_id\n" +
                    "         LEFT JOIN directors d ON fd.director_id = d.director_id\n" +
                    "WHERE d.name ILIKE ?\n" +
                    "group by f.FILM_ID\n" +
                    "ORDER BY quantiy desc;";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, filmIdGenres, filmIdMpa, filmIdDirector), request);
        } else throw new NoSuchElementException("Фильм с таким запросом не существует");


    }

    private Film makeFilm(ResultSet rs, Map<Long, Set<FilmGenre>> filmIdGenres, Map<Long, FilmMPA> filmIdMpa, Map<Long, Set<Director>> filmIdDirector) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(Objects.requireNonNull(rs.getTimestamp("release_date")).toLocalDateTime().toLocalDate())
                .duration(rs.getInt("duration"))
                .rate(rs.getInt("rate"))
                .mpa(filmIdMpa.get(rs.getLong("film_id")))
                .genres(filmIdGenres.getOrDefault(rs.getLong("film_id"), new HashSet<>()))
                .directors(filmIdDirector.getOrDefault(rs.getLong("film_id"), new HashSet<>()))
                .build();

    }

    private Map<Long, Set<FilmGenre>> getAllGenre() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT DISTINCT *  FROM FILMS_GENRE fg INNER JOIN GENRE G on FG.genre_id = G.genre_id");
        Map<Long, Set<FilmGenre>> filmsGenres = new HashMap<>();
        long filmId;
        while (rs.next()) {
            FilmGenre genre = FilmGenre.builder()
                    .id(rs.getInt("genre_id"))
                    .name(rs.getString("name"))
                    .build();
            Set<FilmGenre> genres = new HashSet<>();
            filmId = rs.getLong("film_id");
            if (filmsGenres.containsKey(filmId)) {
                genres = filmsGenres.get(filmId);
            }
            genres.add(genre);
            filmsGenres.put(filmId, genres);
        }
        return filmsGenres;
    }

    private Map<Long, FilmMPA> getAllMpa() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT film_id, M.mpa_id, M.TITLE FROM FILMS INNER JOIN MPA M on FILMS.mpa_id = M.mpa_id;");
        Map<Long, FilmMPA> filmIdMpa = new HashMap<>();
        while (rs.next()) {
            FilmMPA mpa = FilmMPA.builder()
                    .name(rs.getString("title"))
                    .id(rs.getInt("mpa_id"))
                    .build();
            filmIdMpa.put(rs.getLong("film_id"), mpa);
        }
        return filmIdMpa;
    }

    private Map<Long, Set<Director>> getAllDirectors() {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT film_id, d.director_id , name FROM  FILMS_DIRECTORS\n" +
                "LEFT JOIN directors d on d.director_id = FILMS_DIRECTORS.director_id\n" +
                "order by film_id;");
        Map<Long, Set<Director>> filmDirectors = new HashMap<>();
        long filmId;
        while (rs.next()) {
            Director director = Director.builder()
                    .id(rs.getInt("director_id"))
                    .name(rs.getString("name"))
                    .build();
            Set<Director> directors = new HashSet<>();
            filmId = rs.getLong("film_id");
            if (filmDirectors.containsKey(filmId)) {
                directors = filmDirectors.get(filmId);
            }
            directors.add(director);
            filmDirectors.put(rs.getLong("film_id"), directors);
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

    @Override
    public Collection<Film> filmsByDirectorSorted(int directorId, String sortBy) {
        directorStorage.getDirectorById(directorId); // for checking up if director exists
        SqlRowSet sql;
        switch (sortBy) {
            case "year":
                sql = jdbcTemplate.queryForRowSet("SELECT f.*" +
                        "FROM films_directors AS fd " +
                        "JOIN FILMS AS f ON fd.film_id = f.film_id " +
                        "WHERE director_id = ? " +
                        "GROUP BY f.film_id, f.release_date " +
                        "ORDER BY f.release_date", directorId);
                break;

            case "likes":
                sql = jdbcTemplate.queryForRowSet("SELECT f.* " +
                        "FROM films_directors as fd " +
                        "JOIN films AS f ON fd.film_id = f.film_id " +
                        "LEFT JOIN users_like AS ul ON f.film_id = ul.film_id " +
                        "WHERE director_id = ? " +
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
}
