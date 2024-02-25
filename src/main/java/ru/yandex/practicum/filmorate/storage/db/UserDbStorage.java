package ru.yandex.practicum.filmorate.storage.db;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Primary
@Repository
@RequiredArgsConstructor
class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Loggable
    public User addUser(User user) {
        user = validationName(user);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long key = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        return findUserById(key);
    }

    @Override
    @Loggable
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
        return findUserById(user.getId());
    }

    @Override
    @Loggable
    public List<User> getUserList() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> findUserById(rs.getLong("id")));
    }

    @Override
    @Loggable
    public User findUserById(long userId) {
        User user;
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (userRow.next()) {
            user = User.builder().id(userRow.getLong("id"))
                    .email(userRow.getString("email"))
                    .name(userRow.getString("name"))
                    .login(userRow.getString("login"))
                    .birthday(Objects.requireNonNull(userRow.getDate("birthday")).toLocalDate())
                    .build();
        } else {
            throw new NoSuchElementException("userId");
        }
        return user;
    }

    @Override
    @Loggable
    public List<User> getFriendsList(long userId) {
        validationIdUser(userId);
        String sqlQuery = "SELECT friend_id FROM user_friends WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                findUserById(rs.getInt("friend_id")), userId);
    }

    @Override
    @Loggable
    public void putFriend(int userId, int friendId) {
        validationIdUser(userId);
        validationIdUser(friendId);
        if (isFriends(userId, friendId)) {
            String sqlQuery = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } else {
            throw new IllegalArgumentException("Данный пользователь уже добавлен в друзья");
        }
    }

    @Override
    @Loggable
    public void deleteFriend(int userId, int friendId) {
        validationIdUser(userId);
        validationIdUser(friendId);
        if (!isFriends(userId, friendId)) {
            String sqlQuery = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } else {
            throw new IllegalArgumentException("Данный пользователь не ваш друг");
        }
    }

    @Override
    @Loggable
    public List<User> getMutualFriends(int userId, int otherId) {
        validationIdUser(userId);
        validationIdUser(otherId);

        String sqlQuery = "SELECT friend_id FROM user_friends WHERE user_id = ? AND  friend_id IN " +
                "(SELECT friend_id FROM user_friends WHERE user_id = ?)";
        Set<User> mutualFriends = new HashSet<>();
        SqlRowSet friends = jdbcTemplate.queryForRowSet(sqlQuery, userId, otherId);

        if (friends.next()) {
            mutualFriends.add(findUserById(friends.getLong("friend_id")));
        }
        return new ArrayList<>(mutualFriends);
    }

    @Loggable
    public void deleteUser(int id) {
        validationIdUser(id);
        String sqlUserLike = "DELETE FROM USERS WHERE id = ?";
        jdbcTemplate.update(sqlUserLike, id);
    }

    @Loggable
    private void validationIdUser(long userId) {
        SqlRowSet sqlUser = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", userId);

        if (!sqlUser.next()) {
            throw new NoSuchElementException("userId");
        }
    }

    @Loggable
    private User validationName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user = user.toBuilder()
                    .name(user.getLogin())
                    .build();
        }
        return user;
    }

    @Loggable
    private boolean isFriends(long userId, long friendId) {
        String sqlQuery = "SELECT * FROM user_friends WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userFriends = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);

        return !userFriends.next();
    }
}
