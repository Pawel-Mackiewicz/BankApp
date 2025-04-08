package info.mackiewicz.bankapp.presentation.dashboard.dto;

import info.mackiewicz.bankapp.account.validation.DifferentAccounts;
import info.mackiewicz.bankapp.account.validation.ValidIban;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
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

    /**
     * Checks if a valid recipient is provided for the internal transfer.
     *
     * <p>This method returns {@code true} if either the recipient IBAN or the recipient email
     * is provided (i.e., non-null and contains non-whitespace characters); otherwise, it returns {@code false}.
     *
     * @return {@code true} if a valid recipient contact detail is present, {@code false} otherwise
     */
    public boolean isValid() {
        // Check if either recipientIban or recipientEmail is provided
        return (recipientIban != null && !recipientIban.trim().isEmpty())
            || (recipientEmail != null && !recipientEmail.trim().isEmpty());
    }
}