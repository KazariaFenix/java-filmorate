package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.IsAfter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
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
    private final Set<FilmGenre> genres;
    private final FilmMPA mpa;
    @Singular("oneLike")
    private final Set<Integer> userLike;
    private final Collection<Director> directors;


    public Map<String, Object> toMap() {
        Map<String, Object> film = new HashMap<>();
        film.put("name", name);
        film.put("description", description);
        film.put("release_date", releaseDate);
        film.put("duration", duration);
        film.put("rate", rate);
        film.put("mpa_id", mpa.getId());
        return film;
    }

}
