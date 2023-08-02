package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    InMemoryUserStorage storage;

    @Autowired
    UserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public User getUserById(int userId) {
        if (storage.getUserMap().containsKey(userId)) {
            return storage.getUserMap().get(userId);
        } else {
            throw new NoSuchElementException("userId");
        }
    }

    public void putFriend(int userId, int friendId) {
        if (!storage.getUserMap().containsKey(userId)) {
            throw new NoSuchElementException("userId");
        }
        if (!storage.getUserMap().containsKey(friendId)) {
            throw new NoSuchElementException("friendId");
        }
        User user = storage.getUserMap().get(userId);
        User friend = storage.getUserMap().get(friendId);

        if (user.getFriends().contains(friendId)) {
            throw new IllegalArgumentException("Данный пользователь уже добавлен в друзья");
        }
        storage.getUserMap().put(userId, user.toBuilder().friend(friendId).build());
        storage.getUserMap().put(friendId, friend.toBuilder().friend(userId).build());
    }

    public void deleteFriend(int userId, int friendId) {
        if (!storage.getUserMap().containsKey(userId)) {
            throw new NoSuchElementException("userId");
        }
        if (!storage.getUserMap().containsKey(friendId)) {
            throw new NoSuchElementException("friendId");
        }
        User user = storage.getUserMap().get(userId);
        User friend = storage.getUserMap().get(friendId);

        if (!user.getFriends().contains(friendId)) {
            throw new IllegalArgumentException("Данный пользователь еще не добавлен в друзья");
        }
        final List<Integer> userList = user.getFriends().stream()
                .filter(integer -> !integer.equals(friendId))
                .collect(Collectors.toList());
        final List<Integer> friendList = friend.getFriends().stream()
                .filter(integer -> !integer.equals(userId))
                .collect(Collectors.toList());
        storage.getUserMap().put(userId, user.toBuilder().clearFriends().friends(userList).build());
        storage.getUserMap().put(friendId, friend.toBuilder().clearFriends().friends(friendList).build());

    }

    public List<User> getFriends(int userId) {
        if (!storage.getUserMap().containsKey(userId)) {
            throw new NoSuchElementException("userId");
        }
        final List<User> friends = new ArrayList<>();
        final User user = storage.getUserMap().get(userId);

        for (Integer friend : user.getFriends()) {
            friends.add(storage.getUserMap().get(friend));
        }
        return friends;
    }

    public List<User> getMutualFriends(int userId, int otherId) {
        if (!storage.getUserMap().containsKey(userId)) {
            throw new NoSuchElementException("userId");
        }
        if (!storage.getUserMap().containsKey(otherId)) {
            throw new NoSuchElementException("otherId");
        }
        final List<User> mutualFriends = new ArrayList<>();
        final User user = storage.getUserMap().get(userId);
        final User other = storage.getUserMap().get(otherId);

        for (Integer friend : user.getFriends()) {
            if (other.getFriends().contains(friend)) {
                mutualFriends.add(storage.getUserMap().get(friend));
            }
        }
        return mutualFriends;
    }
}
