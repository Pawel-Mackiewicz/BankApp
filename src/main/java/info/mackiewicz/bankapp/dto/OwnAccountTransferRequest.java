package info.mackiewicz.bankapp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OwnAccountTransferRequest {
    @NotNull(message = "Source account is required")
    private Integer sourceAccountId;

    @NotNull(message = "Destination account is required")
    private Integer destinationAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private String amount;

    @NotEmpty(message = "Title is required")
    private String title;
}