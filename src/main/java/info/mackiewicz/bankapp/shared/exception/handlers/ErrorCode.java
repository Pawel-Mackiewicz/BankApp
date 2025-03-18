package info.mackiewicz.bankapp.shared.exception.handlers;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Common Errors
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation failed. Please check your input and try again."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Requested resource not found."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later."),
    
    // Security Errors
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Reset link could not be found. Please request a new reset link."),
    TOKEN_EXPIRED(HttpStatus.GONE, "This password reset link has expired. Please request a new one."),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "Invalid password reset link"),
    TOKEN_USED(HttpStatus.GONE, "This password reset link has already been used. Please request a new one."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Your login credentials are invalid. Please try again."),
    TOO_MANY_PASSWORD_RESET_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "You've reached the limit of password reset attempts. Please try again later."),
    
    // User Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "We couldn't find an account with the provided information.");
    
    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
