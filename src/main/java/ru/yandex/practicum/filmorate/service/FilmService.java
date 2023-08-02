package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final InMemoryUserStorage userStorage;
    private final InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryUserStorage userStorage, InMemoryFilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Film getFilmById(int filmId) {
        if (filmStorage.getFilmMap().containsKey(filmId)) {
            return filmStorage.getFilmMap().get(filmId);
        } else {
            throw new NoSuchElementException("filmId");
        }
    }

    public void putLike(int filmId, int userId) {
        if (!userStorage.getUserMap().containsKey(userId)) {
            throw new NoSuchElementException("userId");
        }
        if (!filmStorage.getFilmMap().containsKey(filmId)) {
            throw new NoSuchElementException("filmId");
        }
        Film film = filmStorage.getFilmMap().get(filmId);

        if (film.getUserLike().contains(userId)) {
            throw new IllegalArgumentException("Пользователь уже поставил фильму лайк");
        }
        filmStorage.getFilmMap().put(filmId, film.toBuilder().rate(film.getRate() + 1).oneLike(userId).build());
    }

    public void deleteLike(int filmId, int userId) {
        if (!userStorage.getUserMap().containsKey(userId)) {
            throw new NoSuchElementException("userId");
        }
        if (!filmStorage.getFilmMap().containsKey(filmId)) {
            throw new NoSuchElementException("filmId");
        }
        Film film = filmStorage.getFilmMap().get(filmId);

        if (!film.getUserLike().contains(userId)) {
            throw new IllegalArgumentException("Пользователь еще не поставил фильму лайк");
        }

        List<Integer> filmLike = film.getUserLike().stream().filter(integer -> integer != userId)
                .collect(Collectors.toList());
        filmStorage.getFilmMap().put(filmId, film.toBuilder().rate(film.getRate() - 1)
                .clearUserLike().userLike(filmLike).build());
    }

    public List<Film> getPopularFilm(int count) {
        return filmStorage.getFilmList().stream()
                .sorted(Comparator.comparing(Film::getRate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toList());
    }
}
