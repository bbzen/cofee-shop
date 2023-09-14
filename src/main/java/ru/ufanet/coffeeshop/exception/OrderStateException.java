package ru.ufanet.coffeeshop.exception;

public class OrderStateException extends RuntimeException {
    public OrderStateException(String message) {
        super(message);
    }
}
