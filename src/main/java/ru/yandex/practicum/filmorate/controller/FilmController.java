package ru.yandex.practicum.filmorate.controller;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.config.BinderConfiguration;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;
    private MeterRegistry meterRegistry;

    public FilmController(FilmService service, MeterRegistry meterRegistry) {
        this.service = service;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping
    @Loggable
    @Timed
    public List<Film> getFilmList() {
        meterRegistry.counter("BINDER_COUNTER").increment();
        return service.getFilmList();
    }

    @PostMapping
    @Loggable
    @Timed
    public Film postFilm(@Valid @RequestBody Film film) {
        return service.addFilm(film);
    }

    @PutMapping
    @Loggable
    @Timed
    public Film putFilm(@Valid @RequestBody Film film) {
        return service.updateFilm(film);
    }

    @GetMapping("/{id}")
    @Loggable
    @Timed
    public Film getFilmById(@PathVariable int id) {
        return service.findFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @Loggable
    @Timed
    public void putLike(@PathVariable int id, @PathVariable int userId) {
        service.putLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @Loggable
    @Timed
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    @Loggable
    @Timed
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count,
                                 @RequestParam(defaultValue = "0") int year,
                                 @RequestParam(defaultValue = "0") int genreId) {
        return service.getPopularFilm(count, genreId, year);
    }

    @GetMapping("/common")
    @Loggable
    @Timed
    public List<Film> getCommonFilms(@RequestParam(name = "userId") int userId, @RequestParam(name = "friendId") int friendId) {
        return service.getCommonFilms(userId, friendId);
    }

    @DeleteMapping(value = "/{id}")
    @Loggable
    @Timed
    public void deleteFilm(@Valid @PathVariable int id) {
        service.deleteFilm(id);

    }

    @GetMapping("/director/{directorId}")
    @Loggable
    @Timed
    public Collection<Film> getFilmsDirectors(@Valid @PathVariable int directorId, @RequestParam String sortBy) {
        return service.getFilmsDirectors(directorId, sortBy);
    }

    @GetMapping("/search")
    @Loggable
    @Timed
    public List<Film> searchFilms(@RequestParam(name = "query") String query,
                                  @RequestParam(name = "by") List<String> titleOrDirector) {

        return service.searchFilms(query, titleOrDirector);
    }
}
