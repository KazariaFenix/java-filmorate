package ru.yandex.practicum.filmorate.service.db;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.model.FilmMPA;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
class MPAServiceDb implements MpaService {
    private final MPAStorage mpa;

    @Override
    @Loggable
    public List<FilmMPA> getAllMPA() {
        return mpa.getAllMPA();
    }

    @Override
    @Loggable
    public FilmMPA getMPAById(long mpaId) {
        return mpa.getMPAById(mpaId);
    }
}
