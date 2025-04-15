package info.mackiewicz.bankapp.shared.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;
import info.mackiewicz.bankapp.shared.web.dto.interfaces.ApiErrorResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Standard implementation of the ApiErrorResponse interface for error handling in REST API responses.
 *
 * <p>This class provides a structured format for error responses, including:
 * <ul>
 *   <li>HTTP status code</li>
 *   <li>Error title/category</li>
 *   <li>Detailed error message</li>
 *   <li>Request path where the error occurred</li>
 *   <li>Timestamp of the error</li>
 * </ul></p>
 *
 * <p>The class can be instantiated either with individual error components or with a predefined
 * {@link ErrorCode}. It ensures consistent error response format across the application.</p>
 *
 * <p>This class is thread-safe as it's immutable after construction.</p>
 *
 * @see ApiErrorResponse
 * @see ErrorCode
 * @see info.mackiewicz.bankapp.shared.core.ApiExceptionHandler
 */
@Getter
public class BaseApiError implements ApiErrorResponse {
    /** The HTTP status code representing the type of error */
    @Schema(example = "BAD_REQUEST")
    private final HttpStatus status;
    
    /** Category or type of the error */
    @Schema(example = "VALIDATION_ERROR")
    private final String title;
    
    /** Detailed description of the error */
    @Schema(example = "Validation failed. Please check your input and try again.")
    private final String message;
    
    /** The URI path where the error occurred */
    private final String path;
    
    /**
     * Timestamp when the error occurred
     * Formatted as dd-MM-yyyy HH:mm:ss in JSON responses
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private final LocalDateTime timestamp;

    /**
     * Creates a new error response with manual status, title and message.
     *
     * @param status the HTTP status code for the error
     * @param title the error title/category
     * @param message the detailed error message
     * @param path the request path where the error occurred
     */
    public BaseApiError(HttpStatus status, String title, String message, String path) {
        this.status = status;
        this.title = title;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a new error response with manual status, title and message without a path.
     * Useful for non-request specific errors.
     *
     * @param status the HTTP status code for the error
     * @param title the error title/category
     * @param message the detailed error message
     */
    public BaseApiError(HttpStatus status, String title, String message) {
        this.status = status;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = null;
    }

    /**
     * Creates a new error response from an ErrorCode enum.
     *
     * @param errorCode the predefined error code
     * @param path the request path where the error occurred
     */
    public BaseApiError(ErrorCode errorCode, String path) {
        this.status = errorCode.getStatus();
        this.title = errorCode.name();
        this.message = errorCode.getMessage();
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}