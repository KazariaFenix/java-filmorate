package ru.yandex.practicum.filmorate.controller;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.aspect.Loggable;
import ru.yandex.practicum.filmorate.model.FilmMPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MPAController {
    private final MpaService mpa;
    private MeterRegistry meterRegistry;

    public MPAController(MpaService mpa, MeterRegistry meterRegistry) {
        this.mpa = mpa;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping
    @Loggable
    public List<FilmMPA> getAllMPA() {
        return mpa.getAllMPA();
    }

    @GetMapping("/{id}")
    @Loggable
    public FilmMPA getMPAById(@PathVariable int id) {
        meterRegistry.counter("GET_MPA_BY_ID", List.of(Tag.of("id", String.valueOf(id))))
                .increment();
        return mpa.getMPAById(id);
    }
}
