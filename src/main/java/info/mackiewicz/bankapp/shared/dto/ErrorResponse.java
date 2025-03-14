package info.mackiewicz.bankapp.shared.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private int status;
}