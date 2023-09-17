package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {
    boolean deleteDirectorById(int id);

    Director putDirector(Director director);

    Director postDirector(Director director);

    Director getDirectorById(int id);

    Collection<Director> getAllDirectors();
}
