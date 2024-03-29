package ru.yandex.practicum.filmorate.service.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
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
    public List<FilmMPA> getAllMPA() {
        return mpa.getAllMPA();
    }

    @Override
    public FilmMPA getMPAById(long mpaId) {
        return mpa.getMPAById(mpaId);
    }
}
