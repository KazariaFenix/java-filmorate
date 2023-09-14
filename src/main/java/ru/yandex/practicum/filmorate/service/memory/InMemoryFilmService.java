package ru.yandex.practicum.filmorate.service.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;


import java.util.*;

@Service
@RequiredArgsConstructor
class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;

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
    public List<Film> searchFilms(String query, List<String> by) {
        return null;
    }

    @Override
    public void deleteFilm(int id) {
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return null;
    }

    @Override
    public Collection<Film> getFilmsDirectors(int directorId, String sortType) {
        return null;
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
