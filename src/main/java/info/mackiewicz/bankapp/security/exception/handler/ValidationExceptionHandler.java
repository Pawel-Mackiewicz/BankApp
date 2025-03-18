package info.mackiewicz.bankapp.security.exception.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import info.mackiewicz.bankapp.shared.dto.ValidationApiError;
import info.mackiewicz.bankapp.shared.dto.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ValidationExceptionHandler {

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

        private String getRequestURI(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
