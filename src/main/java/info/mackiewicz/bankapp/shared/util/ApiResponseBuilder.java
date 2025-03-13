package info.mackiewicz.bankapp.shared.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

/**
 * Utility class for building consistent HTTP responses across the application.
 * Provides methods for creating standardized response entities with ApiResponse wrapper.
 */
@Component
@RequiredArgsConstructor
public class ApiResponseBuilder {

    /**
     * Creates a response entity with CREATED status
     *
     * @param data The data to be included in the response
     * @return ResponseEntity with ApiResponse wrapper and CREATED status
     * @param <T> Type of the response data
     */
    public <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(data));
    }

    /**
     * Creates a response entity with OK status
     *
     * @param data The data to be included in the response
     * @return ResponseEntity with ApiResponse wrapper and OK status
     * @param <T> Type of the response data
     */
    public <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Creates a response entity for successful deletion (OK status with no content)
     *
     * @return ResponseEntity with ApiResponse wrapper and OK status
     */
    public ResponseEntity<ApiResponse<Void>> deleted() {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}