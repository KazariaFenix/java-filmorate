package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {

    public Director addDirector(Director director);

    public Director editDirector (Director director);

    public Director getDirectorById (int id);

    public Collection<Director> getAllDirectors();

    public boolean killDirectorById (int id);

    public void setFilmsDirectors(Collection<Director> directors, int filmId);

    public Collection<Director> getFilmDirectorsSet(int filmId);

    public void deleteFilmDirectors(int filmId);
}
