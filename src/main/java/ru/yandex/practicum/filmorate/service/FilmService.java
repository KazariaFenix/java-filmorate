package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    List<Film> getFilmList();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film findFilmById(int filmId);

    List<Film> getPopularFilm(int count, int genreId, int year);

    void deleteLike(int filmId, int userId);

    void putLike(int filmId, int userId);

    List<Film> searchFilms(String query, List<String> by);

    void deleteFilm(int id);
    List<Film> getCommonFilms(int userId, int friendId);
    Collection<Film> getFilmsDirectors(int directorId, String sortType);
}
