package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserManager;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Getter
    UserManager manager = new UserManager();

    @GetMapping
    public List<User> getUserList() {
        return new ArrayList<>(manager.getUserMap().values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (manager.getUserMap().keySet().contains(user.getId())) {
            throw new ValidationException();
        }
        manager.buildIdUser(user);
        manager.buildNameUser(user);
        manager.saveUser(user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!manager.getUserMap().keySet().contains(user.getId())) {
            throw new ValidationException();
        }
        manager.buildNameUser(user);
        manager.getUserMap().put(user.getId(), user);
        return user;
    }
}
