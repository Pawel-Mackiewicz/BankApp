package info.mackiewicz.bankapp.shared.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;
import lombok.Getter;

@Getter
public class ApiError {
    private HttpStatus status;
    private String title;
    private String message;
    private String path;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiError(HttpStatus status, String title, String message) {
        this.status = status;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(ErrorCode errorCode, String path) {
        this.status = errorCode.getStatus();
        this.title = errorCode.name();
        this.message = errorCode.getMessage();
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}