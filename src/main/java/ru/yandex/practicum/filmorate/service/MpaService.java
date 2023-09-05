package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.FilmMPA;

import java.util.List;

public interface MpaService {
    List<FilmMPA> getAllMPA();

    FilmMPA getMPAById(long mpaId);
}
