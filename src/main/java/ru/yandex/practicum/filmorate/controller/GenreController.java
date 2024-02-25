package ru.yandex.practicum.filmorate.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genre;

    @GetMapping
    @Loggable
    @Timed("GET_ALL_GENRES")
    public List<FilmGenre> getAllGenres() {
        return genre.getAllGenre();
    }

    @GetMapping("/{id}")
    @Loggable
    @Timed("GET_GENRE_BY_ID")
    public FilmGenre getGenreById(@PathVariable int id) {
        return genre.getGenreById(id);
    }
}
