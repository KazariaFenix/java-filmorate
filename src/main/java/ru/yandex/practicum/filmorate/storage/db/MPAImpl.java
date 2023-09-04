package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.FilmMPA;
import ru.yandex.practicum.filmorate.storage.MPADao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@Primary
public class MPAImpl implements MPADao {
    private final JdbcTemplate jdbcTemplate;

    public MPAImpl(JdbcTemplate jdbcTemplate) {
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
        }else {
            throw new NoSuchElementException("МРА с таким айди не существует");
        }
    }
}
