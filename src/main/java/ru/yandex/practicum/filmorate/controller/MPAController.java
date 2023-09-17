package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmMPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MPAController {
    private final MpaService mpa;

    @GetMapping
    public List<FilmMPA> getAllMPA() {
        return mpa.getAllMPA();
    }

    @GetMapping("/{id}")
    public FilmMPA getMPAById(@PathVariable int id) {
        return mpa.getMPAById(id);
    }
}
