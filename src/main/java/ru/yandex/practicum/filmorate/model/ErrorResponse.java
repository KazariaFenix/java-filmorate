package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class ErrorResponse {
    String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

}
