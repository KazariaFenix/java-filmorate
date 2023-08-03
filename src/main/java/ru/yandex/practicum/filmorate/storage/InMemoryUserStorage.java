package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> userMap = new LinkedHashMap();
    private int idUser = 0;

    private User buildIdUser(User user) {
        idUser++;
        return user.toBuilder().id(idUser).friends(new ArrayList<>()).build();
    }

    private User buildNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user = user.toBuilder().name(user.getLogin()).friends(user.getFriends()).build();
        }
        if (user.getFriends() == null) {
            user = user.toBuilder().friends(userMap.get(user.getId()).getFriends()).build();
        }
        return user;
    }

    @Override
    public List<User> getUserList() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User addUser(User user) {
        if (userMap.containsKey(user.getId())) {
            throw new NoSuchElementException("user");
        }
        user = buildIdUser(user);
        user = buildNameUser(user);
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!userMap.containsKey(user.getId())) {
            throw new IllegalArgumentException("Данный юзер не существует");
        }
        user = buildNameUser(user);
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(int userId) {
        return userMap.get(userId);
    }

    @Override
    public void putUser(int userId, User user) {
        userMap.put(userId, user);
    }
}
