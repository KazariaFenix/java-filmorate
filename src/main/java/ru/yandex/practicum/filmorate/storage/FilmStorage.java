package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getFilmList();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(int filmId);

    void putFilm(int filmId, Film film);
}
