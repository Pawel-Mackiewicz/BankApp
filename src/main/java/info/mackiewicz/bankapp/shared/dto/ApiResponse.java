package info.mackiewicz.bankapp.shared.dto;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@Builder(setterPrefix = "with")
public class ApiResponse<T> {
    T data;
    String message;
    HttpStatus status;

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