package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmMPA;

import java.util.List;

public interface MPADao {
    List<FilmMPA> getAllMPA();

    FilmMPA getMPAById(long mpaId);
}
