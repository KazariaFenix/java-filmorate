package ru.yandex.practicum.filmorate.service.db;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventStatus;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
@Primary
@RequiredArgsConstructor
class UserServiceDb implements UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;
    private final FilmStorage filmStorage;

    @Override
    @Loggable
    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    @Override
    @Loggable
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    @Loggable
    public List<User> getUserList() {
        return userStorage.getUserList();
    }

    @Override
    @Loggable
    public User findUserById(long userId) {
        return userStorage.findUserById(userId);
    }

    @Override
    @Loggable
    public List<User> getFriendsList(long userId) {
        return userStorage.getFriendsList(userId);
    }

    @Override
    @Loggable
    public void putFriend(int userId, int friendId) {
        userStorage.putFriend(userId, friendId);
        eventStorage.addEvent(friendId, userId, EventType.FRIEND, EventStatus.ADD);
    }

    @Override
    @Loggable
    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
        eventStorage.addEvent(friendId, userId, EventType.FRIEND, EventStatus.REMOVE);
    }

    @Override
    @Loggable
    public List<User> getMutualFriends(int userId, int otherId) {
        return userStorage.getMutualFriends(userId, otherId);
    }

    @Override
    @Loggable
    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    @Override
    @Loggable
    public List<Event> getUserFeeds(int userId) {
        return eventStorage.getAllEventByUserId(userId);
    }

    @Loggable
    public List<Film> getRecommendedFilms(int userId) {
        return filmStorage.getRecommendedFilms(userId);
    }
}
