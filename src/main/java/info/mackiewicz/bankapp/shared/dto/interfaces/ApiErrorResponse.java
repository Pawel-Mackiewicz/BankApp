package info.mackiewicz.bankapp.shared.dto.interfaces;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public interface ApiErrorResponse {
    HttpStatus getStatus();
    String getTitle();
    String getMessage();
    String getPath();
    LocalDateTime getTimestamp();
}
