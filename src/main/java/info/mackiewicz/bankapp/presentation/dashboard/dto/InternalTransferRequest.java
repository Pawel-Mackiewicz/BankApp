package info.mackiewicz.bankapp.presentation.dashboard.dto;

import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.shared.annotations.DifferentAccounts;
import info.mackiewicz.bankapp.shared.annotations.ValidIban;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@DifferentAccounts
public class InternalTransferRequest implements WebTransferRequest {
    
    @ValidIban
    @NotNull(message = "Source IBAN is required")
    private String sourceIban;

    @ValidIban
    private String recipientIban;

    @Email(message = "Invalid email format")
    private String recipientEmail;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private String amount;

    @NotEmpty(message = "Title is required")
    private String title;

    private TransactionType transactionType = TransactionType.TRANSFER_INTERNAL;

    public boolean isValid() {
        // Check if either recipientIban or recipientEmail is provided
        return (recipientIban != null && !recipientIban.trim().isEmpty())
            || (recipientEmail != null && !recipientEmail.trim().isEmpty());
    }
}