package info.mackiewicz.bankapp.shared.dto;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import info.mackiewicz.bankapp.shared.dto.interfaces.ApiErrorResponse;
import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;
import lombok.Getter;

/**
 * Standard implementation of the ApiErrorResponse interface.
 * Provides common error response functionality including HTTP status, message, and timestamp.
 *
 * @see ApiErrorResponse
 * @see ErrorCode
 */
@Getter
public class BaseApiError implements ApiErrorResponse {
    private HttpStatus status;
    private String title;
    private String message;
    private String path;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

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