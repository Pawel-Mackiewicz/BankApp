package info.mackiewicz.bankapp.shared.web.dto;

import lombok.Value;

/**
 * Immutable value class that encapsulates details about a single field validation error.
 *
 * <p>This class represents a single validation failure in the API, containing information about:
 * <ul>
 *   <li>The field that failed validation</li>
 *   <li>A user-friendly message explaining the validation failure</li>
 *   <li>The actual value that was rejected by the validation</li>
 * </ul></p>
 *
 * <p>Used as part of {@link ValidationApiError} to provide detailed information
 * about validation failures in API requests. The immutability is guaranteed by
 * Lombok's {@code @Value} annotation.</p>
 *
 * @see ValidationApiError
 * @see info.mackiewicz.bankapp.shared.web.error.validation.ValidationErrorProcessor
 */
@Value
public class ValidationError {
    /**
     * Name of the field that failed validation.
     * This could be a single field name (e.g., "email") or a path to a nested field
     * (e.g., "user.address.zipCode").
     */
    String field;

    /**
     * User-friendly description explaining why the validation failed.
     * Should provide clear guidance on how to fix the validation error
     * (e.g., "Email must be a valid email address").
     */
    String message;

    /**
     * The actual value that was rejected by the validation.
     * This helps clients identify which specific input caused the validation failure.
     * May be null if the field was missing entirely.
     */
    String rejectedValue;
}
