package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserManager;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserManager manager = new UserManager();

    @GetMapping
    public List<User> getUserList() {
        return manager.getUserList();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        return manager.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return manager.updateUser(user);
    }
}
