package info.mackiewicz.bankapp.user.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import info.mackiewicz.bankapp.shared.validation.ValidationErrorResponse;

/**
 * Provides validation functionality for HTTP requests in the user context.
 * Handles validation results and converts them into appropriate HTTP responses.
 * This service is thread-safe as it maintains no state.
 */
@Service
public class RequestValidator {
    
    /**
     * Processes validation results and generates an appropriate HTTP response.
     * If validation errors are present, creates a structured error response.
     *
     * @param bindingResult Spring's binding result containing validation outcomes
     * @return ResponseEntity with validation errors if present, null if validation passed
     * @see ValidationErrorResponse#fromBindingResult(BindingResult)
     */
    public ResponseEntity<?> validateRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ValidationErrorResponse.fromBindingResult(bindingResult);
        }
        return null;
    }
    
    /**
     * Checks if there are any validation errors in the binding result.
     * Provides a convenient way to verify validation state without generating a response.
     *
     * @param bindingResult Spring's binding result containing validation outcomes
     * @return true if validation errors exist, false otherwise
     * @see #validateRequest(BindingResult)
     */
    public boolean hasErrors(BindingResult bindingResult) {
        return bindingResult.hasErrors();
    }
}