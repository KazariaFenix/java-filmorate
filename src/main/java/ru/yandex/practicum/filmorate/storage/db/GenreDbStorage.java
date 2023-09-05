package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Primary
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FilmGenre> getAllGenre() {
        String sqlQuery = "SELECT * FROM genre";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs));
    }

    private FilmGenre makeGenre(ResultSet rs) throws SQLException {
        return FilmGenre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public FilmGenre getGenreById(long genreId) {
        String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlQuery, genreId);

        if (genreRow.next()) {
            return FilmGenre.builder()
                    .id(genreRow.getInt("genre_id"))
                    .name(genreRow.getString("name"))
                    .build();
        } else {
            throw new NoSuchElementException("Жанра под таким идентификатором не существует");
        }
    }
}
