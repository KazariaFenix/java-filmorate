package ru.yandex.practicum.filmorate.exception;

public class NoSuchElementException extends RuntimeException {
    String param;

    public NoSuchElementException(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
