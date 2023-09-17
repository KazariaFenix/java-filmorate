package ru.yandex.practicum.filmorate.exception;

public class ErrorUpdatingListDirectors extends RuntimeException {
    String param;

    public ErrorUpdatingListDirectors(String param) {
        this.param = param;
    }
}
