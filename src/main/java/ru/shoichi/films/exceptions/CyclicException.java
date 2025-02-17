package ru.shoichi.films.exceptions;

public class CyclicException extends RuntimeException {
    public CyclicException(String message) {
        super(message);
    }
}
