package info.mackiewicz.bankapp.shared.web.dto;

import lombok.Value;

/**
 * Encapsulates details about a single field validation error.
 * Used as part of ValidationApiError to provide specific information 
 * about which fields failed validation and why.
 */
@Value
public class ValidationError {
    /**
     * Name of the field that failed validation
     */
    String field;         

    /**
     * Description of why the validation failed
     */
    String message;       

    /**
     * The value that was rejected by validation
     */
    String rejectedValue; 
}
