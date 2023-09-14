package ru.ufanet.coffeeshop.exception;

public class EventValidationException extends RuntimeException {
    public EventValidationException(String message) {
        super(message);
    }
}