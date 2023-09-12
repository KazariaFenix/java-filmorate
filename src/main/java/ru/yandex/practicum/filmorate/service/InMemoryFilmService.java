package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;

@Service
public class InMemoryFilmService implements FilmService {
    private final InMemoryFilmStorage filmStorage;

    @Autowired
    public InMemoryFilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public List<Film> getFilmList() {
        return filmStorage.getFilmList();
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    @Override
    public Film findFilmById(int filmId) {
        return filmStorage.findFilmById(filmId);
    }

    @Override
    public void putLike(int filmId, int userId) {
        filmStorage.putLike(filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilm(int count, int genreId, int year) {
        return filmStorage.getPopularFilm(count, genreId, year);
    }
}
