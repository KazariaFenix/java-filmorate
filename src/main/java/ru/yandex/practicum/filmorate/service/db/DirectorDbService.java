package ru.yandex.practicum.filmorate.service.db;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
class DirectorDbService implements DirectorService {
    private final DirectorStorage directorStorage;
    private static final String ERROR_MESSAGE = "Director id='%s' was not found";

    @Loggable
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
    @Loggable
    public boolean deleteDirectorById(int id) {
        Director dir = valid(directorStorage.getDirectorById(id));
        if (directorStorage.deleteDirectorById(dir.getId())) {
            return true;
        } else {
            throw new NoSuchElementException(String.format(ERROR_MESSAGE, id));
        }
    }

    @Override
    @Loggable
    public Director putDirector(Director director) {
        Director dir = directorStorage.editDirector(valid(director));
        if (dir != null) {
            return dir;
        } else {
            throw new NoSuchElementException(String.format(ERROR_MESSAGE, director.getId()));
        }
    }

    @Override
    @Loggable
    public Director postDirector(Director director) {
        return directorStorage.addDirector(valid(director));
    }

    @Override
    @Loggable
    public Director getDirectorById(int id) {
        if (id <= 0) {
            throw new NoSuchElementException(String.format(ERROR_MESSAGE, id));
        }
        if (directorStorage.getDirectorById(id) != null) {
            return directorStorage.getDirectorById(id);
        } else {
            throw new NoSuchElementException(String.format(ERROR_MESSAGE, id));
        }
    }

    @Override
    @Loggable
    public Collection<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }
}
