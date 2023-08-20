package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Film getFilmById(int filmId) {
        if (filmStorage.getFilm(filmId) != null) {
            return filmStorage.getFilm(filmId);
        } else {
            throw new NoSuchElementException("filmId");
        }
    }

    public void putLike(int filmId, int userId) {
        if (userStorage.getUser(userId) == null) {
            throw new NoSuchElementException("userId");
        }
        if (filmStorage.getFilm(filmId) == null) {
            throw new NoSuchElementException("filmId");
        }
        Film film = filmStorage.getFilm(filmId);

        if (film.getUserLike().contains(userId)) {
            throw new IllegalArgumentException("Пользователь уже поставил фильму лайк");
        }
        film = film.toBuilder()
                .rate(film.getRate() + 1)
                .oneLike(userId)
                .build();

        filmStorage.putFilm(filmId, film);
    }

    public void deleteLike(int filmId, int userId) {
        if (userStorage.getUser(userId) == null) {
            throw new NoSuchElementException("userId");
        }
        if (filmStorage.getFilm(filmId) == null) {
            throw new NoSuchElementException("filmId");
        }
        Film film = filmStorage.getFilm(filmId);

        if (!film.getUserLike().contains(userId)) {
            throw new IllegalArgumentException("Пользователь еще не поставил фильму лайк");
        }

        List<Integer> filmLike = film.getUserLike()
                .stream()
                .filter(integer -> integer != userId)
                .collect(Collectors.toList());
        film = film.toBuilder()
                .rate(film.getRate() - 1)
                .clearUserLike()
                .userLike(filmLike)
                .build();
        filmStorage.putFilm(filmId, film);
    }

    public List<Film> getPopularFilm(int count) {
        return filmStorage.getFilmList().stream()
                .sorted(Comparator.comparing(Film::getRate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toList());
    }
}
