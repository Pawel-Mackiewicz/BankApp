package info.mackiewicz.bankapp.shared.web.error.validation;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import info.mackiewicz.bankapp.shared.web.dto.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@Component
public class ValidationErrorProcessor {

    public List<ValidationError> extractValidationErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::convert)
                .toList();
    }

    public List<ValidationError> extractValidationErrors(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(this::convert)
                .toList();
    }

    public ValidationError convert(ConstraintViolation<?> violation) {
        return new ValidationError(
                violation.getPropertyPath().toString(),
                violation.getMessage(),
                violation.getInvalidValue() != null ? violation.getInvalidValue().toString() : "");
    }

    @SuppressWarnings("null")
    public ValidationError convert(FieldError fieldError) {
        return new ValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "");
    }
}
