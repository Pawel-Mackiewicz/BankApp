package info.mackiewicz.bankapp.shared.web.error.validation;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import info.mackiewicz.bankapp.shared.web.dto.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Processes validation errors from different sources and converts them into a
 * standardized
 * ValidationError format. Handles both method argument validation failures and
 * constraint
 * violations.
 * 
 * @see info.mackiewicz.bankapp.shared.web.dto.ValidationError
 */
@Component
public class ValidationErrorProcessor {

    /**
     * Extracts validation errors from a MethodArgumentNotValidException.
     * Converts field errors into a list of ValidationError objects.
     *
     * @param ex the method argument validation exception to process
     * @return list of ValidationError objects containing field names, error
     *         messages, and rejected values
     * @see org.springframework.web.bind.MethodArgumentNotValidException
     * @see info.mackiewicz.bankapp.shared.web.dto.ValidationError
     */
    public List<ValidationError> extractValidationErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::convert)
                .toList();
    }

    /**
     * Extracts validation errors from a ConstraintViolationException.
     * Converts constraint violations into a list of ValidationError objects.
     *
     * @param ex the constraint violation exception to process
     * @return list of ValidationError objects containing property paths, error
     *         messages, and invalid values
     * @see jakarta.validation.ConstraintViolationException
     * @see info.mackiewicz.bankapp.shared.web.dto.ValidationError
     */
    public List<ValidationError> extractValidationErrors(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .map(this::convert)
                .toList();
    }

    /**
     * Converts a single constraint violation into a ValidationError object.
     *
     * @param violation the constraint violation to convert
     * @return ValidationError containing the property path, message, and invalid
     *         value
     * @see jakarta.validation.ConstraintViolation
     * @see info.mackiewicz.bankapp.shared.web.dto.ValidationError
     */
    private ValidationError convert(ConstraintViolation<?> violation) {
        return new ValidationError(
                violation.getPropertyPath().toString(),
                violation.getMessage(),
                violation.getInvalidValue() != null ? violation.getInvalidValue().toString() : "");
    }

    /**
     * Converts a single field error into a ValidationError object.
     * Suppresses null warnings as Spring's FieldError methods are annotated for
     * null safety.
     *
     * @param fieldError the field error to convert
     * @return ValidationError containing the field name, error message, and
     *         rejected value
     * @see org.springframework.validation.FieldError
     * @see info.mackiewicz.bankapp.shared.web.dto.ValidationError
     */
    @SuppressWarnings("null")
    private ValidationError convert(FieldError fieldError) {
        return new ValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "");
    }
}
