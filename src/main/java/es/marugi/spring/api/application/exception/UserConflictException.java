package es.marugi.spring.api.application.exception;

public class UserConflictException extends RuntimeException {
    public UserConflictException() {
        super("Login or email is already in use");
    }
}