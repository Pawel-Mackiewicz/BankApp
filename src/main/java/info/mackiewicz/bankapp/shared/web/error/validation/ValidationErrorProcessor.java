package info.mackiewicz.bankapp.shared.web.error.validation;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
     * Extracts and converts both field and global validation errors from a
     * MethodArgumentNotValidException into a list of ValidationError objects.
     *
     * <p>This method processes the binding result of the exception by mapping each field error
     * using the convert method and each global error using the convertGlobal method, then
     * concatenates them into a single list.</p>
     *
     * @param ex the MethodArgumentNotValidException containing validation errors
     * @return a combined list of ValidationError objects representing field and global errors
     * @see org.springframework.web.bind.MethodArgumentNotValidException
     * @see info.mackiewicz.bankapp.shared.web.dto.ValidationError
     */
    public List<ValidationError> extractValidationErrors(MethodArgumentNotValidException ex) {
        var bindingResult = ex.getBindingResult();
        
        var fieldErrors = bindingResult.getFieldErrors()
                .stream()
                .map(this::convert)
                .toList();
                
        var globalErrors = bindingResult.getGlobalErrors()
                .stream()
                .map(this::convertGlobal)
                .toList();
                
        return Stream.concat(fieldErrors.stream(), globalErrors.stream())
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

    /**
     * Converts a global error (class-level constraint violation) into a ValidationError object.
     *
     * @param globalError the global error to convert
     * @return ValidationError containing the object name as field, error message, and empty rejected value
     */
    private ValidationError convertGlobal(ObjectError globalError) {
        return new ValidationError(
                globalError.getObjectName(),
                globalError.getDefaultMessage(),
                "");
    }
}
