package info.mackiewicz.bankapp.shared.web.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

/**
 * Represents a standardized API response structure for the application.
 * Provides a consistent way to format all API responses with optional data payload,
 * status code and message. Uses builder pattern for convenient instance creation.
 *
 * @param <T> the type of data payload in the response
 * 
 * @see HttpStatus
 */
@Builder(setterPrefix = "with")
public class ApiResponse<T> {
    private final T data;
    private final String message;
    private final HttpStatus status;

    /**
     * Returns the data payload of the response.
     *
     * @return the data payload of type T, can be null
     */
    public T getData() {
        return data;
    }

    /**
     * Returns the message associated with the response.
     *
     * @return the response message, can be null
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the HTTP status code of the response.
     *
     * @return the HTTP status code
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * Creates a success response with HTTP status 200 (OK).
     *
     * @param <T> the type of the data payload
     * @param data the data to include in the response
     * @return a new ApiResponse instance with the provided data and OK status
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .withData(data)
                .withStatus(HttpStatus.OK)
                .build();
    }

    /**
     * Creates a success response with HTTP status 201 (Created).
     * Typically used for successful resource creation operations.
     *
     * @param <T> the type of the data payload
     * @param data the data to include in the response
     * @return a new ApiResponse instance with the provided data and CREATED status
     */
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .withData(data)
                .withStatus(HttpStatus.CREATED)
                .build();
    }

    /**
     * Creates an error response with a custom message and HTTP status.
     *
     * @param <T> the type of the data payload (not used in error responses)
     * @param message the error message to include in the response
     * @param status the HTTP status code for the error
     * @return a new ApiResponse instance with the provided error message and status
     */
    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .withMessage(message)
                .withStatus(status)
                .build();
    }
}