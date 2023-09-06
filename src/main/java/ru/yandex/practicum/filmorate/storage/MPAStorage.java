package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmMPA;

import java.util.List;

public interface MPAStorage {
    List<FilmMPA> getAllMPA();

    FilmMPA getMPAById(long mpaId);
}
