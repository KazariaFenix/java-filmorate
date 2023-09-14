package ru.yandex.practicum.filmorate.storage.memory;


import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmMap = new LinkedHashMap<>();
    private int idFilm = 0;

    @Override
    public List<Film> getFilmList() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film addFilm(Film film) {
        if (filmMap.containsKey(film.getId())) {
            throw new NoSuchElementException("film");
        }
        film = buildFilm(film);
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!filmMap.containsKey(film.getId())) {
            throw new IllegalArgumentException("Данный фильм не существует");
        }
        film = buildLikeOfFilm(film);

        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film findFilmById(int filmId) {
        if (filmMap.get(filmId) != null) {
            return filmMap.get(filmId);
        } else {
            throw new NoSuchElementException("filmId");
        }
    }

    @Override
    public void putLike(int filmId, int userId) {
        Film film = findFilmById(filmId);

        if (film.getUserLike().contains(userId)) {
            throw new IllegalArgumentException("Пользователь уже поставил фильму лайк");
        }
        film = film.toBuilder()
                .rate(film.getRate() + 1)
                .oneLike(userId)
                .build();

        filmMap.put(filmId, film);
    }

    @Override
    public Collection<Film> filmsByDirectorSorted(int directorId, String sortBy) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return null;
    }

    @Override
    public List<Film> getRecommendedFilms(int userId) {
        return null;
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Film film = findFilmById(filmId);

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

        filmMap.put(filmId, film);
    }

    @Override
    public List<Film> getPopularFilm(int count, int genreId, int year) {
        return getFilmList().stream()
                .sorted(Comparator.comparing(Film::getRate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFilm(int id) {
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        return null;
    }

    private Film buildFilm(Film film) {
        ++idFilm;
        if (film.getRate() == null) {
            return film.toBuilder().id(idFilm).userLike(new ArrayList<>()).rate(0).build();
        }
        return film.toBuilder().id(idFilm).userLike(new ArrayList<>()).build();
    }

    private Film buildLikeOfFilm(Film film) {
        if (film.getRate() == null) {
            return film.toBuilder().userLike(new ArrayList<>()).rate(0).build();
        }
        final Film oldFilm = filmMap.get(film.getId());
        final List<Integer> userLike = new ArrayList<>(oldFilm.getUserLike());
        return film.toBuilder().clearUserLike().userLike(userLike).build();
    }
}
