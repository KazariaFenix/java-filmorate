package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;

@Service
public class InMemoryUserService implements UserService {
    private final InMemoryUserStorage storage;

    @Autowired
    InMemoryUserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<User> getUserList() {
        return storage.getUserList();
    }

    @Override
    public User addUser(User user) {
        return storage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    @Override
    public User findUserById(long userId) {
        return storage.findUserById(userId);
    }

    @Override
    public List<User> getFriendsList(long userId) {
        return storage.getFriendsList(userId);
    }

    @Override
    public void putFriend(int userId, int friendId) {
        storage.putFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        storage.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherId) {
        return storage.getMutualFriends(userId, otherId);
    }
}
