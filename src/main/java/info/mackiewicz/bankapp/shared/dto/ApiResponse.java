package info.mackiewicz.bankapp.shared.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder(setterPrefix = "with")
public class ApiResponse<T> {
    private final T data;
    private final String message;
    private final HttpStatus status;

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .withData(data)
                .withStatus(HttpStatus.OK)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .withData(data)
                .withStatus(HttpStatus.CREATED)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .withMessage(message)
                .withStatus(status)
                .build();
    }
}