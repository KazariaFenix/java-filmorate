package ru.yandex.practicum.filmorate.service.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
class InMemoryUserService implements UserService {
    private final UserStorage storage;

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

    @Override
    public List<Event> getUserFeeds(int userId) {
        return null;
    }

    @Override
    public void deleteUser(int id) {
    }

    @Override
    public List<Film> getRecommendedFilms(int userId) {
        return null;
    }
}
