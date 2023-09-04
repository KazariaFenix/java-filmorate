package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.db.GenreImpl;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreImpl genre;

    @Autowired
    public GenreController(GenreImpl genre) {
        this.genre = genre;
    }

    @GetMapping
    public List<FilmGenre> getAllGenres() {
        return genre.getAllGenre();
    }

    @GetMapping("/{id}")
    public FilmGenre getGenreById(@PathVariable int id) {
        return genre.getGenreById(id);
    }
}
