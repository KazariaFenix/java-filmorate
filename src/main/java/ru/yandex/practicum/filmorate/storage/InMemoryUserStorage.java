package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private final Map<Long, User> userMap = new LinkedHashMap();
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
    public User findUserById(long userId) {
        if (userMap.get(userId) != null) {
            return userMap.get(userId);
        } else {
            throw new NoSuchElementException("userId");
        }
    }

    public void putUser(long userId, User user) {
        userMap.put(userId, user);
    }
}
