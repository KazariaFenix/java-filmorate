package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;


import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping
    public List<User> getUserList() {
        return service.getUserList();
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        return service.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return service.findUserById(id);
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

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@Valid @PathVariable int id) {
        service.deleteUser(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeeds(@PathVariable int id) {
        return service.getUserFeeds(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendedFilms(@PathVariable int id) {
        return service.getRecommendedFilms(id);
    }
}
