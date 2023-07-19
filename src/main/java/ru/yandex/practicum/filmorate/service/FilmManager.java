package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.LinkedHashMap;
import java.util.Map;

public class FilmManager {
    @Getter
    private final Map<Integer, Film> filmMap = new LinkedHashMap<>();
    private int idFilm = 0;

    public void buildFilm(Film film) {
        idFilm++;
        film.setId(idFilm);
    }
}
