package info.mackiewicz.bankapp.shared.web.dto;

import java.util.List;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;
import info.mackiewicz.bankapp.shared.web.dto.interfaces.ValidationErrorResponse;
import lombok.Getter;

/**
 * Validation-specific implementation of error response that includes field-level validation details.
 * Used for handling validation failures in request processing.
 *
 * @see BaseApiError
 * @see ValidationError
 */
@Getter
public class ValidationApiError extends BaseApiError implements ValidationErrorResponse {

    private final List<ValidationError> errors;

    /**
     * Creates a new validation error response with field-level error details.
     *
     * @param path the request path where validation failed
     * @param errors list of field-specific validation errors
     */
    public ValidationApiError(String path, List<ValidationError> errors) {
        super(ErrorCode.VALIDATION_ERROR, path);
        this.errors = errors;
    }
}
