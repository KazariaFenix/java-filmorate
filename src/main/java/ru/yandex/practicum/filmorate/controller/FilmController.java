package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmManager;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmManager manager = new FilmManager();

    @GetMapping
    public List<Film> getFilmList() {
        return manager.getFilmList();
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        return manager.addFilm(film);
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film film) {
        return manager.updateFilm(film);
    }
}
