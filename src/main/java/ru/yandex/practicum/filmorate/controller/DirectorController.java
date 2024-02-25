package ru.yandex.practicum.filmorate.controller;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService service;

    @GetMapping("/directors")
    @Loggable
    @Timed
    public Collection<Director> getAllDirectors() {
        return service.getAllDirectors();
    }

    @GetMapping("/directors/{id}")
    @Loggable
    @Timed
    public Director getDirectorById(@Valid @PathVariable int id) {
        return service.getDirectorById(id);
    }

    @PostMapping("/directors")
    @Loggable
    @Timed
    public Director postDirector(@Valid @RequestBody Director director) {

        return service.postDirector(director);
    }

    @PutMapping("/directors")
    @Loggable
    @Timed
    public Director putDirector(@Valid @RequestBody Director director) {
        return service.putDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    @Loggable
    @Timed
    public boolean deleteDirector(@PathVariable int id) {
        return service.deleteDirectorById(id);
    }
}
