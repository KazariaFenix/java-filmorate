package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserManager {
    private final Map<Integer, User> userMap = new LinkedHashMap();
    private int idUser = 0;

    public void buildIdUser(User user) {
        idUser++;
        user.setId(idUser);
    }

    public void buildNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> getUserList() {
        return new ArrayList<>(userMap.values());
    }

    public User addUser(User user) {
        if (userMap.keySet().contains(user.getId())) {
            throw new ValidationException();
        }
        buildIdUser(user);
        buildNameUser(user);
        userMap.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        if (!userMap.keySet().contains(user.getId())) {
            throw new ValidationException();
        }
        buildNameUser(user);
        userMap.put(user.getId(), user);
        return user;
    }
}
