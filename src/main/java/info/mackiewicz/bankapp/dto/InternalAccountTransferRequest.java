package info.mackiewicz.bankapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InternalAccountTransferRequest {
    @NotNull(message = "Source account is required")
    private Integer sourceAccountId;

    private String recipientIban;

    @Email(message = "Invalid email format")
    private String recipientEmail;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private String amount;

    @NotEmpty(message = "Title is required")
    private String title;

    public boolean isValid() {
        // Albo IBAN albo email musi być podany
        return (recipientIban != null && !recipientIban.trim().isEmpty())
            || (recipientEmail != null && !recipientEmail.trim().isEmpty());
    }
}