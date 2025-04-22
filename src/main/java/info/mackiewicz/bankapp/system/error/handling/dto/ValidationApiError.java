package info.mackiewicz.bankapp.system.error.handling.dto;

import java.util.List;

import info.mackiewicz.bankapp.system.error.handling.core.ApiExceptionHandler;
import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;
import info.mackiewicz.bankapp.system.error.handling.dto.interfaces.ValidationErrorResponse;
import lombok.Getter;

/**
 * Specialized error response for validation failures that extends BaseApiError with field-level validation details.
 *
 * <p>This class enhances the basic error response by adding a collection of specific validation
 * errors for individual fields. It is used when request validation fails due to invalid field
 * values, missing required fields, or constraint violations.</p>
 *
 * <p>The validation response includes:
 * <ul>
 *   <li>All standard error response fields from {@link BaseApiError}</li>
 *   <li>A list of {@link ValidationError} objects detailing each field-level validation failure</li>
 * </ul></p>
 *
 * <p>This class is thread-safe as it's immutable after construction.</p>
 *
 * @see BaseApiError
 * @see ValidationError
 * @see ValidationErrorResponse
 * @see ApiExceptionHandler#handleMethodArgumentNotValidException
 * @see ApiExceptionHandler#handleConstraintViolationException
 */
@Getter
public class ValidationApiError extends BaseApiError implements ValidationErrorResponse {

    /**
     * List of field-specific validation errors.
     * Each error contains details about which field failed validation and why.
     */
    private final List<ValidationError> errors;

    /**
     * Creates a new validation error response with field-level error details.
     * Automatically sets the error code to {@link ErrorCode#VALIDATION_ERROR} and
     * initializes the base error response fields through the parent constructor.
     *
     * @param path the request path where validation failed
     * @param errors list of field-specific validation errors, must not be null
     * @throws IllegalArgumentException if errors list is null
     */
    public ValidationApiError(String path, List<ValidationError> errors) {
        super(ErrorCode.VALIDATION_ERROR, path);
        this.errors = errors;
    }
}
