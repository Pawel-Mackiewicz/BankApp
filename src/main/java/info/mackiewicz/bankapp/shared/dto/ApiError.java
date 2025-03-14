package info.mackiewicz.bankapp.shared.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class ApiError {
    private HttpStatus status;
    private String title;
    private String message;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiError(HttpStatus status, String title, String message) {
        this.status = status;
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}