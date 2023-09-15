package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NoSuchElementException extends RuntimeException {
    String param;

    public NoSuchElementException(String param) {
        this.param = param;
    }

}
