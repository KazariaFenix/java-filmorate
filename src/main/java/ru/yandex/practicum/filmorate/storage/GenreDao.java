package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface GenreDao {
    List<FilmGenre> getAllGenre();

    FilmGenre getGenreById(long genreId);
}
