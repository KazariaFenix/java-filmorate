package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.db.UserServiceDb;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServiceDb service;

    @Autowired
    public UserController(UserServiceDb service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getUserList() {
        return service.getUserStorage().getUserList();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        return service.getUserStorage().addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return service.getUserStorage().updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return service.getUserStorage().findUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void putFriend(@PathVariable int id, @PathVariable int friendId) {
        service.putFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        service.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return service.getFriendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        return service.getMutualFriends(id, otherId);
    }
}
