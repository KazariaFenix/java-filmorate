package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.FilmMPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Primary
public class MPADbStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPADbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FilmMPA> getAllMPA() {
        String sqlQuery = "SELECT * FROM mpa";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMpa(rs));
    }

    private FilmMPA makeMpa(ResultSet rs) throws SQLException {
        return FilmMPA.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("title"))
                .build();
    }

    @Override
    public FilmMPA getMPAById(long mpaId) {
        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet(sqlQuery, mpaId);

        if (mpaRow.next()) {
            return FilmMPA.builder()
                    .id(mpaRow.getInt("mpa_id"))
                    .name(mpaRow.getString("title"))
                    .build();
        } else {
            throw new NoSuchElementException("МРА с таким айди не существует");
        }
    }
}
