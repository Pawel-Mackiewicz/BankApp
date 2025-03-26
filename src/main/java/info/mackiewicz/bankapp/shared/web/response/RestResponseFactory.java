package info.mackiewicz.bankapp.shared.web.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Utility class for building consistent HTTP responses across the application.
 * Provides methods for creating standardized response entities with ApiResponse wrapper.
 */
@Component
@RequiredArgsConstructor
public class RestResponseFactory {

    /**
     * Creates a response entity with CREATED status
     *
     * @param data The data to be included in the response
     * @return ResponseEntity with ApiResponse wrapper and CREATED status
     * @param <T> Type of the response data
     */
    public <T> ResponseEntity<RestResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestResponse.created(data));
    }

    /**
     * Creates a response entity with OK status
     *
     * @param data The data to be included in the response
     * @return ResponseEntity with ApiResponse wrapper and OK status
     * @param <T> Type of the response data
     */
    public <T> ResponseEntity<RestResponse<T>> ok(T data) {
        return ResponseEntity.ok(RestResponse.success(data));
    }

    /**
     * Creates a response entity for successful deletion (OK status with no content)
     *
     * @return ResponseEntity with ApiResponse wrapper and OK status
     */
    public ResponseEntity<RestResponse<Void>> deleted() {
        return ResponseEntity.ok(RestResponse.success(null));
    }
}