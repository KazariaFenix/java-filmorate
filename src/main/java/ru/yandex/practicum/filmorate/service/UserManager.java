package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserManager {
    @Getter
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

    public void saveUser(User user) {
        userMap.put(user.getId(), user);
    }
}
