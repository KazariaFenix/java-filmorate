package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.IsAfter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private int id;
    @Email
    @NotBlank
    private String email;
    private String name;
    @NotBlank
    private String login;
    @IsAfter(current = "1900_1_1")
    @Past
    private LocalDate birthday;
}
