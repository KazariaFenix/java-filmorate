package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FilmManager {
    private final Map<Integer, Film> filmMap = new LinkedHashMap<>();
    private int idFilm = 0;

    public void buildFilm(Film film) {
        idFilm++;
        film.setId(idFilm);
    }

    public List<Film> getFilmList() {
        return new ArrayList<>(filmMap.values());
    }

    public Film addFilm(Film film) {
        if (filmMap.keySet().contains(film.getId())) {
            throw new ValidationException();
        }
        buildFilm(film);
        filmMap.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (!filmMap.containsKey(film.getId())) {
            throw new ValidationException();
        }
        filmMap.put(film.getId(), film);
        return film;
    }
}
