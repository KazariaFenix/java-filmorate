package ru.yandex.practicum.filmorate.service.db;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.model.EventStatus;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
class FilmServiceDb implements FilmService {
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    @Override
    @Loggable
    public List<Film> getFilmList() {
        return filmStorage.getFilmList();
    }

    @Override
    @Loggable
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    @Loggable
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    @Override
    @Loggable
    public Film findFilmById(int filmId) {
        return filmStorage.findFilmById(filmId);
    }

    @Override
    @Loggable
    public void putLike(int filmId, int userId) {
        filmStorage.putLike(filmId, userId);
        eventStorage.addEvent(filmId, userId, EventType.LIKE, EventStatus.ADD);
    }

    @Override
    @Loggable
    public List<Film> searchFilms(String query, List<String> by) {
        return filmStorage.searchFilms(query, by);
    }

    @Override
    @Loggable
    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmId, userId);
        eventStorage.addEvent(filmId, userId, EventType.LIKE, EventStatus.REMOVE);
    }

    @Override
    @Loggable
    public List<Film> getPopularFilm(int count, int genreId, int year) {
        return filmStorage.getPopularFilm(count, genreId, year);
    }

    @Loggable
    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }

    @Loggable
    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    @Loggable
    public Collection<Film> getFilmsDirectors(int directorId, String sortType) {
        return filmStorage.filmsByDirectorSorted(directorId, sortType);
    }
}
