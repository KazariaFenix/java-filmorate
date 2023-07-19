package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmManager;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmManager manager = new FilmManager();

    @GetMapping
    public List<Film> getFilmList() {
        return new ArrayList<>(manager.getFilmMap().values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (manager.getFilmMap().keySet().contains(film.getId())) {
            throw new ValidationException();
        }
        manager.buildFilm(film);
        manager.getFilmMap().put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!manager.getFilmMap().containsKey(film.getId())) {
            throw new ValidationException();
        }
        manager.getFilmMap().put(film.getId(), film);
        return film;
    }
}
