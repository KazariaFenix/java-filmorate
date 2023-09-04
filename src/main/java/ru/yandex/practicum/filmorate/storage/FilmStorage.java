package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilmList();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film findFilmById(int filmId);
}
