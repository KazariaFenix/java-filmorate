package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUserList();

    User addUser(User user);

    User updateUser(User user);

    User findUserById(long userId);

    List<User> getFriendsList(long userId);

    void putFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getMutualFriends(int userId, int otherId);

    void deleteUser(int id);
}
