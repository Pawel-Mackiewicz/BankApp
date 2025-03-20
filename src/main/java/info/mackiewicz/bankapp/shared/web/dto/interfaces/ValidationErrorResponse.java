package info.mackiewicz.bankapp.shared.web.dto.interfaces;

import java.util.List;

import info.mackiewicz.bankapp.shared.web.dto.ValidationApiError;
import info.mackiewicz.bankapp.shared.web.dto.ValidationError;

/**
 * Extends the basic error response interface to include validation-specific error details.
 * This interface is used for responses that need to communicate multiple field-level validation failures.
 * 
 * @see ApiErrorResponse
 * @see ValidationApiError
 * @see ValidationError
 */
public interface ValidationErrorResponse extends ApiErrorResponse {
    /**
     * Returns a list of validation errors that occurred during request processing.
     * Each error contains information about the field that failed validation,
     * the validation message, and the rejected value.
     *
     * @return list of validation errors with field-specific details
     */
    List<ValidationError> getErrors();
}
