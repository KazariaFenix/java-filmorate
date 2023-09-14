package ru.yandex.practicum.filmorate.service.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.NoSuchElementException;

@Service
@Primary
@RequiredArgsConstructor
class DirectorDbService implements DirectorService {
    private final DirectorStorage storage;
    private static final String ERROR_MESSAGE = "Director id='%s' was not found";

    private Director valid(Director director) {
        if (director == null) {
            throw new ValidationException("Director is null");
        }
        if (director.getId() > 0) {
            if (!director.getName().isBlank() && !director.getName().isEmpty()) {
                return director;
            } else {
                throw new ValidationException("Director's name shouldn't be empty");
            }
        } else {
            throw new ValidationException("Director's id should be more than 0");
        }
    }

    @Override
    public boolean deleteDirectorById(int id) {
        Director dir = valid(storage.getDirectorById(id));
        if (storage.deleteDirectorById(dir.getId())) {
            return true;
        } else {
            throw new NoSuchElementException(String.format(ERROR_MESSAGE, id));
        }
    }

    @Override
    public Director putDirector(Director director) {
        Director dir = storage.editDirector(valid(director));
        if (dir != null) {
            return dir;
        } else {
            throw new NoSuchElementException(String.format(ERROR_MESSAGE, director.getId()));
        }
    }

    @Override
    public Director postDirector(Director director) {
        return storage.addDirector(valid(director));
    }

    @Override
    public Director getDirectorById(int id) {
        if (id <= 0) {
            throw new NoSuchElementException(String.format(ERROR_MESSAGE, id));
        }
        if (storage.getDirectorById(id) != null) {
            return storage.getDirectorById(id);
        } else {
            throw new NoSuchElementException(String.format(ERROR_MESSAGE, id));
        }
    }

    @Override
    public Collection<Director> getAllDirectors() {
        return storage.getAllDirectors();
    }


}
