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
     * Determines if the transfer request contains sufficient recipient information.
     *
     * <p>This method validates the request by checking whether either the recipient IBAN or the recipient email
     * is provided as a non-null and non-empty string, ensuring that at least one valid recipient identifier is specified.</p>
     *
     * @return true if either the recipient IBAN or the recipient email is provided and non-empty; false otherwise
     */
    public boolean isValid() {
        // Check if either recipientIban or recipientEmail is provided
        return (recipientIban != null && !recipientIban.trim().isEmpty())
            || (recipientEmail != null && !recipientEmail.trim().isEmpty());
    }
}