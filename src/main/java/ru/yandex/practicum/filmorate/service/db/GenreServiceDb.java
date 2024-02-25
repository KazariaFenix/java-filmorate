package ru.yandex.practicum.filmorate.service.db;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
class GenreServiceDb implements GenreService {
    private final GenreStorage genre;

    @Override
    @Loggable
    public List<FilmGenre> getAllGenre() {
        return genre.getAllGenre();
    }

    @Override
    @Loggable
    public FilmGenre getGenreById(long genreId) {
        return genre.getGenreById(genreId);
    }
}
