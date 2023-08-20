package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Getter
    private final UserStorage storage;

    @Autowired
    UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User getUserById(int userId) {
        if (storage.getUser(userId) != null) {
            return storage.getUser(userId);
        } else {
            throw new NoSuchElementException("userId");
        }
    }

    public void putFriend(int userId, int friendId) {
        if (storage.getUser(userId) == null) {
            throw new NoSuchElementException("userId");
        }
        if (storage.getUser(friendId) == null) {
            throw new NoSuchElementException("friendId");
        }
        User user = storage.getUser(userId);
        User friend = storage.getUser(friendId);

        if (user.getFriends().containsKey(friendId)) {
            throw new IllegalArgumentException("Данный пользователь уже добавлен в друзья");
        }
        user = user.toBuilder()
                .friend(friendId, true)
                .build();
        friend = friend.toBuilder()
                .friend(userId, true)
                .build();

        storage.putUser(userId, user);
        storage.putUser(friendId, friend);
    }

    public void deleteFriend(int userId, int friendId) {
        if (storage.getUser(userId) == null) {
            throw new NoSuchElementException("userId");
        }
        if (storage.getUser(friendId) == null) {
            throw new NoSuchElementException("friendId");
        }
        User user = storage.getUser(userId);
        User friend = storage.getUser(friendId);

        if (!user.getFriends().containsKey(friendId)) {
            throw new IllegalArgumentException("Данный пользователь еще не добавлен в друзья");
        }
        final Map<Integer, Boolean> userFriends = new LinkedHashMap<>(user.getFriends());
        userFriends.remove(friendId);

        final Map<Integer, Boolean> otherFriends = new LinkedHashMap<>(friend.getFriends());
        otherFriends.remove(userId);

        user = user.toBuilder()
                .clearFriends()
                .friends(userFriends)
                .build();
        friend = friend.toBuilder()
                .clearFriends()
                .friends(otherFriends)
                .build();

        storage.putUser(userId, user);
        storage.putUser(friendId, friend);
    }

    public List<User> getFriends(int userId) {
        if (storage.getUser(userId) == null) {
            throw new NoSuchElementException("userId");
        }
        final List<User> friends = new ArrayList<>();
        final User user = storage.getUser(userId);

        for (Integer friend : user.getFriends().keySet()) {
            friends.add(storage.getUser(friend));
        }
        return friends;
    }

    public List<User> getMutualFriends(int userId, int otherId) {
        if (storage.getUser(userId) == null) {
            throw new NoSuchElementException("userId");
        }
        if (storage.getUser(otherId) == null) {
            throw new NoSuchElementException("otherId");
        }
        final List<User> mutualFriends = new ArrayList<>();
        final User user = storage.getUser(userId);
        final User other = storage.getUser(otherId);

        for (Integer friend : user.getFriends().keySet()) {
            if (other.getFriends().containsKey(friend)) {
                mutualFriends.add(storage.getUser(friend));
            }
        }
        return mutualFriends;
    }
}
