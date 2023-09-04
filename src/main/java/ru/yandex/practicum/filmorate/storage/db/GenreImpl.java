package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Primary
public class GenreImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreImpl(JdbcTemplate jdbcTemplate) {
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
