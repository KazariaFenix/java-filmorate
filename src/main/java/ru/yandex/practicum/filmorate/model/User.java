package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.IsAfter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class User {
    private final int id;
    @Email
    @NotBlank
    private final String email;
    private final String name;
    @NotBlank
    private final String login;
    @IsAfter(current = "1900_1_1")
    @Past
    private final LocalDate birthday;
    @Singular
    private final Map<Integer, Boolean> friends;
}
