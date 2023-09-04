package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceInt {
    @Getter
    private final InMemoryUserStorage storage;

    @Autowired
    UserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<User> getFriendsList(long userId) {
        final List<User> friends = new ArrayList<>();
        final User user = storage.findUserById(userId);

        for (Integer friend : user.getFriends()) {
            friends.add(storage.findUserById(friend));
        }
        return friends;
    }

    @Override
    public void putFriend(int userId, int friendId) {
        User user = storage.findUserById(userId);
        User friend = storage.findUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            throw new IllegalArgumentException("Данный пользователь уже добавлен в друзья");
        }

        user = user.toBuilder()
                .friend(friendId)
                .build();
        friend = friend.toBuilder()
                .friend(userId)
                .build();

        storage.putUser(userId, user);
        storage.putUser(friendId, friend);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = storage.findUserById(userId);
        User friend = storage.findUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            throw new IllegalArgumentException("Данный пользователь еще не добавлен в друзья");
        }
        final List<Integer> userList = user.getFriends().stream()
                .filter(integer -> !integer.equals(friendId))
                .collect(Collectors.toList());
        final List<Integer> friendList = friend.getFriends().stream()
                .filter(integer -> !integer.equals(userId))
                .collect(Collectors.toList());
        user = user.toBuilder()
                .clearFriends()
                .friends(userList)
                .build();
        friend = friend.toBuilder()
                .clearFriends()
                .friends(friendList)
                .build();

        storage.putUser(userId, user);
        storage.putUser(friendId, friend);
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherId) {
        final List<User> mutualFriends = new ArrayList<>();
        final User user = storage.findUserById(userId);
        final User other = storage.findUserById(otherId);

        for (Integer friend : user.getFriends()) {
            if (other.getFriends().contains(friend)) {
                mutualFriends.add(storage.findUserById(friend));
            }
        }
        return mutualFriends;
    }
}
