package ru.yandex.practicum.filmorate.service.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
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
    public List<FilmGenre> getAllGenre() {
        return genre.getAllGenre();
    }

    @Override
    public FilmGenre getGenreById(long genreId) {
        return genre.getGenreById(genreId);
    }
}
