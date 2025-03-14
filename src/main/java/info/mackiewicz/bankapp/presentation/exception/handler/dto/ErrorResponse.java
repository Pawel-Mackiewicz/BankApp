package info.mackiewicz.bankapp.presentation.exception.handler.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final String title;
    private final String message;
    private final LocalDateTime timestamp;
    private final String path;
    private final int status;
    private final String error;

    public ErrorResponse(String title, String message, String path, int status, String error) {
        this.title = title;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = path;
        this.status = status;
        this.error = error;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}