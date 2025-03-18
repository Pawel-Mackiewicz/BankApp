package info.mackiewicz.bankapp.security.exception.handler;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import info.mackiewicz.bankapp.shared.dto.ValidationError;
import jakarta.validation.ConstraintViolation;

@Component
public class ValidationErrorProcessor {

      public ValidationError convertConstraintViolation(ConstraintViolation<?> violation) {
        return new ValidationError(
            violation.getPropertyPath().toString(),
            violation.getMessage(),
            violation.getInvalidValue() != null ? 
                violation.getInvalidValue().toString() : null
        );
    }

    public ValidationError convertFieldError(FieldError fieldError) {
        return new ValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                String.valueOf(fieldError.getRejectedValue()));
    }

}
