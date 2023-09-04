package ru.yandex.practicum.filmorate.service.db;

import lombok.Getter;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserServiceInt;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Primary
public class UserServiceDb implements UserServiceInt {
    private final JdbcTemplate jdbc;
    @Getter
    private final UserStorage userStorage;

    public UserServiceDb(JdbcTemplate jdbc, UserStorage userStorage) {
        this.jdbc = jdbc;
        this.userStorage = userStorage;
    }

    @Override
    public List<User> getFriendsList(long userId) {
        validationIdUser(userId);

        String sqlQuery = "SELECT friend_id FROM user_friends WHERE user_id = ?";
        List<User> friends = jdbc.query(sqlQuery, (rs, rowNum) ->
                userStorage.findUserById(rs.getInt("friend_id")), userId);
        return friends;
    }

    @Override
    public void putFriend(int userId, int friendId) {
        validationIdUser(userId);
        validationIdUser(friendId);
        if (isFriends(userId, friendId)) {
            String sqlQuery = "MERGE INTO user_friends (user_id, friend_id) VALUES (?, ?)";
            jdbc.update(sqlQuery, userId, friendId);
        } else {
            throw new IllegalArgumentException("Данный пользователь уже добавлен в друзья");
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        validationIdUser(userId);
        validationIdUser(friendId);
        if (!isFriends(userId, friendId)) {
            String sqlQuery = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
            jdbc.update(sqlQuery, userId, friendId);
        } else {
            throw new IllegalArgumentException("Данный пользователь не ваш друг");
        }
    }

    private boolean isFriends(long userId, long friendId) {
        String sqlQuery = "SELECT * FROM user_friends WHERE user_id = ? AND friend_id = ?";
        SqlRowSet userFriends = jdbc.queryForRowSet(sqlQuery, userId, friendId);

        if (!userFriends.next()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherId) {
        validationIdUser(userId);
        validationIdUser(otherId);

        String sqlQuery = "SELECT friend_id FROM user_friends WHERE user_id = ? AND  friend_id IN " +
                "(SELECT friend_id FROM user_friends WHERE user_id = ?)";
        Set<User> mutualFriends = new HashSet<>();
        SqlRowSet friends = jdbc.queryForRowSet(sqlQuery, userId, otherId);

        if (friends.next()) {
            mutualFriends.add(userStorage.findUserById(friends.getLong("friend_id")));
        }
        return new ArrayList<>(mutualFriends);
    }

    private void validationIdUser(long userId) {
        SqlRowSet sqlUser = jdbc.queryForRowSet("SELECT * FROM users WHERE id = ?", userId);

        if (!sqlUser.next()) {
            throw new NoSuchElementException("Пользователь с таким айди не найден");
        }
    }
}
