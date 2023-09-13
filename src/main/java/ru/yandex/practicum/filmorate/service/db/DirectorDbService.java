package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class DirectorDbService implements DirectorService {

    private final DirectorStorage directorStorage;
    private static final String ERROR_MESSAGE = "Director id='%s' was not found";

    @Autowired
    DirectorDbService(DirectorDbStorage directorDbStorage) {
        this.directorStorage = directorDbStorage;
    }

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
        Director dir = valid(directorStorage.getDirectorById(id));
        if (directorStorage.deleteDirectorById(dir.getId())) {
            return true;
        } else {
            throw new NoSuchElementException(String.format(ERROR_MESSAGE, id));
        }
    }

    @Override
    public Director putDirector(Director director) {
        Director dir = directorStorage.editDirector(valid(director));
        if (dir != null) {
            return dir;
        } else {
            throw new NoSuchElementException(String.format(ERROR_MESSAGE, director.getId()));
        }
    }

    @Override
    public Director postDirector(Director director) {
        return directorStorage.addDirector(valid(director));
    }

    @Override
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
    public Collection<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }


}
