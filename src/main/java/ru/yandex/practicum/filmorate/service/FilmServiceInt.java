package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmServiceInt {
    List<Film> getPopularFilm(int count);

    void deleteLike(int filmId, int userId);

    void putLike(int filmId, int userId);
}
