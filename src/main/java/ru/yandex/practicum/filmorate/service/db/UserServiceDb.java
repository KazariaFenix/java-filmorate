package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventStatus;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.EventDbStorage;

import java.util.*;

@Service
@Primary
public class UserServiceDb implements UserService {
    private final UserStorage userStorage;
    private final EventDbStorage eventStorage;

    @Autowired
    public UserServiceDb(UserStorage userStorage, EventDbStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    @Override
    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    public List<User> getUserList() {
        return userStorage.getUserList();
    }

    @Override
    public User findUserById(long userId) {
        return userStorage.findUserById(userId);
    }

    @Override
    public List<User> getFriendsList(long userId) {
        return userStorage.getFriendsList(userId);
    }

    @Override
    public void putFriend(int userId, int friendId) {
        userStorage.putFriend(userId, friendId);
        eventStorage.addEvent(friendId, userId, EventType.FRIEND, EventStatus.ADD);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
        eventStorage.addEvent(friendId, userId, EventType.FRIEND, EventStatus.REMOVE);
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherId) {
        return userStorage.getMutualFriends(userId, otherId);
    }

    @Override
    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    @Override
    public List<Event> getUserFeeds(int userId) {
        return eventStorage.getAllEventByUserId(userId);
    }
}
