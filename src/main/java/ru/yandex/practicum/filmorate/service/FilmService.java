package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
public class FilmService implements FilmServiceInt {
    private final InMemoryUserStorage userStorage;
    private final InMemoryFilmStorage filmStorage;

    public FilmService(InMemoryUserStorage userStorage, InMemoryFilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public void putLike(int filmId, int userId) {
        Film film = filmStorage.findFilmById(filmId);

        if (film.getUserLike().contains(userId)) {
            throw new IllegalArgumentException("Пользователь уже поставил фильму лайк");
        }
        film = film.toBuilder()
                .rate(film.getRate() + 1)
                .oneLike(userId)
                .build();

        filmStorage.putFilm(filmId, film);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.findFilmById(filmId);

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

    @Override
    public List<Film> getPopularFilm(int count) {
        return filmStorage.getFilmList().stream()
                .sorted(Comparator.comparing(Film::getRate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toList());
    }
}
