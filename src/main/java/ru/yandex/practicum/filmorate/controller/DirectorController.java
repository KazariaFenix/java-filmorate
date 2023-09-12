package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.db.DirectorDbService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
public class DirectorController {

    DirectorDbService service;

    @Autowired
    DirectorController(DirectorDbService dds) {
        this.service = dds;
    }

    @GetMapping("/directors")
    public Collection<Director> getAllDirectors() {
        return service.getAllDirectors();
    }

    @GetMapping("/directors/{id}")
    @Valid
    public Director getDirectorById(@PathVariable int id) {
        return service.getDirectorById(id);
    }

    @PostMapping("/directors")
    public Director postDirector(@Valid @RequestBody Director director) {
        return service.postDirector(director);
    }

    @PutMapping("/directors")
    public Director putDirector(@Valid @RequestBody Director director) {
        return service.putDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public boolean deleteDirector(@PathVariable int id, @RequestBody(required = false) Director director) {
        if (director != null) return service.deleteDirectorById(director.getId());
        else return service.deleteDirectorById(id);
    }


}
