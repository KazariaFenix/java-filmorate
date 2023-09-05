package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FilmMPA;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;

@Service
@Primary
public class MPAServiceDb implements MpaService {
    private final MPAStorage mpa;

    @Autowired
    public MPAServiceDb(MPAStorage mpa) {
        this.mpa = mpa;
    }

    @Override
    public List<FilmMPA> getAllMPA() {
        return mpa.getAllMPA();
    }

    @Override
    public FilmMPA getMPAById(long mpaId) {
        return mpa.getMPAById(mpaId);
    }
}
