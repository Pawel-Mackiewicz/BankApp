package info.mackiewicz.bankapp.exception.handlers;

import info.mackiewicz.bankapp.exception.InvalidUserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "info.mackiewicz.bankapp.controller.api")
public class RestExceptionHandler {

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<String> handleInvalidUserException(InvalidUserException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        return ResponseEntity.internalServerError().body("An unexpected error occurred");
    }
}