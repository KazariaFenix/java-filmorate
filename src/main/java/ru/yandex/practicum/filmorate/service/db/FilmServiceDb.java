package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventStatus;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@Primary
public class FilmServiceDb implements FilmService {
    private final EventStorage eventStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmServiceDb(FilmStorage filmStorage, EventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
    }

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
        eventStorage.addEvent(filmId, userId, EventType.LIKE, EventStatus.ADD);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmId, userId);
        eventStorage.addEvent(filmId, userId, EventType.LIKE, EventStatus.REMOVE);
    }

    @Override
    public List<Film> getPopularFilm(int count, int genreId, int year) {
        return filmStorage.getPopularFilm(count, genreId, year);
    }

    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }
}
