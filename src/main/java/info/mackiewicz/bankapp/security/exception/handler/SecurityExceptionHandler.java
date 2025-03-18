package info.mackiewicz.bankapp.security.exception.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import info.mackiewicz.bankapp.security.exception.ExpiredTokenException;
import info.mackiewicz.bankapp.security.exception.InvalidTokenException;
import info.mackiewicz.bankapp.security.exception.PasswordChangeException;
import info.mackiewicz.bankapp.security.exception.TokenCreationException;
import info.mackiewicz.bankapp.security.exception.TokenNotFoundException;
import info.mackiewicz.bankapp.security.exception.TokenValidationException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.security.exception.UsedTokenException;
import info.mackiewicz.bankapp.shared.dto.BaseApiError;
import info.mackiewicz.bankapp.shared.dto.ValidationApiError;
import info.mackiewicz.bankapp.shared.dto.ValidationError;
import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "info.mackiewicz.bankapp.security.controller")
public class SecurityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseApiError> handleException(Exception ex, WebRequest request) {
        String path = getRequestURI(request);

        ErrorCode errorCode = mapExceptionToError(ex);
        BaseApiError error = new BaseApiError(errorCode, path);
        logError(errorCode, ex, path);

        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationApiError> handleValidationException(MethodArgumentNotValidException ex,
            WebRequest request) {
        String path = getRequestURI(request);

        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::convertFieldError)
                .toList();

        ValidationApiError apiError = new ValidationApiError(path, errors);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationApiError> handleValidationException(ConstraintViolationException ex,
            WebRequest request) {
        String path = getRequestURI(request);

        List<ValidationError> errors = ex.getConstraintViolations()
                .stream()
                .map(this::convertConstraintViolation)
                .toList();

        ValidationApiError apiError = new ValidationApiError(path, errors);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    private ValidationError convertConstraintViolation(ConstraintViolation<?> violation) {
        return new ValidationError(
            violation.getPropertyPath().toString(),
            violation.getMessage(),
            violation.getInvalidValue() != null ? 
                violation.getInvalidValue().toString() : null
        );
    }

    private ValidationError convertFieldError(FieldError fieldError) {
        return new ValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                String.valueOf(fieldError.getRejectedValue()));
    }

    private ErrorCode mapExceptionToError(Exception ex) {
        return switch (ex) {
            // Security Exceptions
            case TokenNotFoundException e -> ErrorCode.TOKEN_NOT_FOUND;
            case ExpiredTokenException e -> ErrorCode.TOKEN_EXPIRED;
            case UsedTokenException e -> ErrorCode.TOKEN_USED;
            case TokenCreationException e -> ErrorCode.INTERNAL_ERROR;
            case TokenValidationException e -> ErrorCode.INTERNAL_ERROR;
            case TooManyPasswordResetAttemptsException e -> ErrorCode.TOO_MANY_PASSWORD_RESET_ATTEMPTS;
            case BadCredentialsException e -> ErrorCode.INVALID_CREDENTIALS;
            case InvalidTokenException e -> ErrorCode.TOKEN_INVALID;

            // User Exceptions
            case UserNotFoundException e -> ErrorCode.USER_NOT_FOUND;
            case PasswordChangeException e -> ErrorCode.INTERNAL_ERROR;

            // Validation Exceptions
            case MethodArgumentNotValidException e -> ErrorCode.VALIDATION_ERROR;
            case ConstraintViolationException e -> ErrorCode.VALIDATION_ERROR;

            // Fallback
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }

    private void logError(ErrorCode error, Exception ex, String path) {
        String message = String.format(
                "Error occurred: %s, Path: %s, Message: %s",
                error.name(),
                path,
                ex.getMessage());

        if (error == ErrorCode.INTERNAL_ERROR) {
            log.error(message, ex);
        } else {
            log.warn(message);
        }
    }

    private String getRequestURI(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
