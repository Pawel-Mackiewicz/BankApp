package info.mackiewicz.bankapp.dto;

import info.mackiewicz.bankapp.model.TransactionType;
import info.mackiewicz.bankapp.validation.DifferentAccounts;
import info.mackiewicz.bankapp.validation.Iban;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@DifferentAccounts
public class InternalTransferRequest implements TransferRequest {
    
    @Iban
    @NotNull(message = "Source IBAN is required")
    private String sourceIban;

    @Iban
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
        // Albo IBAN albo email musi być podany
        return (recipientIban != null && !recipientIban.trim().isEmpty())
            || (recipientEmail != null && !recipientEmail.trim().isEmpty());
    }
}