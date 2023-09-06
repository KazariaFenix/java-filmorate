package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Primary
public class GenreServiceDb implements GenreService {
    private final GenreStorage genre;

    @Autowired
    public GenreServiceDb(GenreStorage genre) {
        this.genre = genre;
    }

    @Override
    public List<FilmGenre> getAllGenre() {
        return genre.getAllGenre();
    }

    @Override
    public FilmGenre getGenreById(long genreId) {
        return genre.getGenreById(genreId);
    }
}
