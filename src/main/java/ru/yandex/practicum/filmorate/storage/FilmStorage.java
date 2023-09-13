package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    List<Film> getFilmList();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film findFilmById(int filmId);

    List<Film> getPopularFilm(int count);

    void deleteLike(int filmId, int userId);

    void putLike(int filmId, int userId);

    public Collection<Film> filmsByDirectorSorted(int directorId, String sortBy);
}
