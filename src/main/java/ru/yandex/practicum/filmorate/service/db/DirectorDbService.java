package ru.yandex.practicum.filmorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.DirectorDbStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

public class DirectorDbService {

    DirectorDbStorage directorDbStorage;
    String errorMessage = "Director id='%s' was not found";

    @Autowired
    DirectorDbService(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    /*
    GET /films/director/{directorId}?sortBy=[year,likes]
    ✅ GET /directors - Список всех режиссёров
    ✅ GET /directors/{id}- Получение режиссёра по id
    ✅ POST /directors - Создание режиссёра
    ✅ PUT /directors - Изменение режиссёра
    DELETE /directors/{id} - Удаление режиссёра
     */

    private Director valid(Director director) {
        if (director == null) throw new ValidationException("Director is null");
        if (director.getId() > 0) {
            if (!director.getName().isBlank() || !director.getName().isEmpty()) {
                return director;
            } else throw new ValidationException("Director's name shouldn't be empty");
        } else throw new ValidationException("Director's id should be more than 0");
    }

    public boolean deleteDirectorById(int id) {
        Director dir = valid(directorDbStorage.getDirectorById(id));
        if (directorDbStorage.killDirectorById(dir.getId())) return true;
        else throw new NoSuchElementException(String.format(errorMessage, id));
    }

    public Director putDirector(Director director) {
        Director dir = directorDbStorage.editDirector(valid(director));
        if (dir != null) return dir;
        else throw new NoSuchElementException(String.format(errorMessage, director.getId()));
    }

    public Director postDirector(Director director) {
        return directorDbStorage.addDirector(valid(director));
    }

    public Director getDirectorById (int id) {
        if (id <= 0) throw new NoSuchElementException(String.format(errorMessage, id));
        if (directorDbStorage.getDirectorById(id) != null) {
            return directorDbStorage.getDirectorById(id);
        } else throw new NoSuchElementException(String.format(errorMessage, id));
    }

    public Collection<Director> getAllDirectors() {
        return directorDbStorage.getAllDirectors();
    }


}
