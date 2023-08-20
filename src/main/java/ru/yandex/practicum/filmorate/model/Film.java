package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.IsAfter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class Film {
    private final int id;
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    @IsAfter(current = "1895_12_28")
    private final LocalDate releaseDate;
    @Positive
    private final Integer duration;
    private final Integer rate;
    private final List<FilmGenre> genre;
    private final FilmRating rating;
    @Singular("oneLike")
    private final List<Integer> userLike;
}
