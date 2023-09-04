package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserServiceInt {
    List<User> getFriendsList(long userId);

    void putFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getMutualFriends(int userId, int otherId);
}
