package org.fizz_buzz.exception;

public class EntityAlreadyExists extends RuntimeException {

    private final String message;

    public EntityAlreadyExists(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "%s already exists".formatted(message);
    }
}
