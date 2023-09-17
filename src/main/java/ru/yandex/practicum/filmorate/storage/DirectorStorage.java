package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {

     Director addDirector(Director director);

     Director editDirector(Director director);

     Director getDirectorById(int id);

     Collection<Director> getAllDirectors();

     boolean deleteDirectorById(int id);

     void setFilmsDirectors(Collection<Director> directors, int filmId);

     Collection<Director> getFilmDirectorsSet(int filmId);

     void deleteFilmDirectors(int filmId);
}
