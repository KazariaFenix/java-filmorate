package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchElementException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    @Getter
    private final Map<Integer, Film> filmMap = new LinkedHashMap<>();
    private int idFilm = 0;

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
        final List<Integer> userLike = oldFilm.getUserLike().stream().collect(Collectors.toList());
        return film.toBuilder().clearUserLike().userLike(userLike).build();
    }

    @Override
    public List<Film> getFilmList() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film addFilm(Film film) {
        if (filmMap.keySet().contains(film.getId())) {
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

    public void putFilm(int filmId, Film film) {
        filmMap.put(filmId, film);
    }
}
