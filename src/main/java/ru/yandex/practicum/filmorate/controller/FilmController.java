package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.db.FilmServiceDb;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmServiceDb service;

    @Autowired
    public FilmController(FilmServiceDb service) {
        this.service = service;
    }

    @GetMapping
    public List<Film> getFilmList() {
        return service.getFilmList();
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        return service.addFilm(film);
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        return service.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return service.findFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable int id, @PathVariable int userId) {
        service.putLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular") //popular?count=1
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count,
                                 @RequestParam(defaultValue = "0") int year,
                                 @RequestParam(defaultValue = "0") int genreId) {
        return service.getPopularFilm(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") int userId, @RequestParam(name = "friendId") int friendId) {
        return service.getCommonFilms(userId, friendId);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteFilm(@Valid @PathVariable int id) {
        service.deleteFilm(id);

    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsdirectors(@Valid @PathVariable int directorId, @RequestParam String sortBy) {
        return service.getFilmsDirectors(directorId, sortBy);
    }
}
