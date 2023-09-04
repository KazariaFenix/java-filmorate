package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        user = validationName(user);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long key = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        User newUser = findUserById(key);

        return newUser;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET email = ?, name = ?, login = ?, birthday = ? WHERE id = ?";
        user = validationName(user);

        validationIdUser(user.getId());
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getName(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        User newUser = findUserById(user.getId());

        return newUser;
    }

    @Override
    public List<User> getUserList() {
        String sqlQuery = "SELECT * FROM users";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> findUserById(rs.getLong("id")));
    }

    @Override
    public User findUserById(long userId) {
        User user;
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, userId);

        if (userRow.next()) {
            user = User.builder().id(userRow.getLong("id"))
                    .email(userRow.getString("email"))
                    .name(userRow.getString("name"))
                    .login(userRow.getString("login"))
                    .birthday(userRow.getDate("birthday").toLocalDate())
                    .build();
        } else {
            throw new NoSuchElementException("Пользователь не найден");
        }

        return user;
    }

    private void validationIdUser(long userId) {
        SqlRowSet sqlUser = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", userId);

        if (!sqlUser.next()) {
            throw new NoSuchElementException("Пользователь с таким айди не найден");
        }
    }

    private User validationName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user = user.toBuilder()
                    .name(user.getLogin())
                    .build();
        }

        return user;
    }
}
