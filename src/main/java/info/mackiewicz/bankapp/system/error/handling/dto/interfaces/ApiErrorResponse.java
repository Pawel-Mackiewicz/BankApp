package info.mackiewicz.bankapp.system.error.handling.dto.interfaces;

import java.time.LocalDateTime;

import info.mackiewicz.bankapp.system.error.handling.dto.BaseApiError;
import org.springframework.http.HttpStatus;

/**
 * Defines the contract for API error responses in the application.
 * This interface provides a standardized structure for error reporting across the API.
 *
 * @see BaseApiError
 * @see ValidationErrorResponse
 */
public interface ApiErrorResponse {
    /**
     * Returns the HTTP status code associated with the error.
     * 
     * @return the HTTP status of the error response
     */
    HttpStatus getStatus();

    /**
     * Returns the title/category of the error.
     * 
     * @return a brief, descriptive title of the error
     */
    String getTitle();

    /**
     * Returns the detailed error message.
     * 
     * @return a human-readable description of the error
     */
    String getMessage();

    /**
     * Returns the API endpoint path where the error occurred.
     * 
     * @return the request path that triggered the error
     */
    String getPath();

    /**
     * Returns the timestamp when the error occurred.
     * 
     * @return the date and time when the error was generated
     */
    LocalDateTime getTimestamp();
}
