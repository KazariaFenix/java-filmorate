package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getUserList();

    User addUser(User user);

    User updateUser(User user);

    User findUserById(long userId);

    List<User> getFriendsList(long userId);

    void putFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getMutualFriends(int userId, int otherId);

    List<Event> getUserFeeds(int userId);

    void deleteUser(int id);

    List<Film> getRecommendedFilms(int userId);
}
