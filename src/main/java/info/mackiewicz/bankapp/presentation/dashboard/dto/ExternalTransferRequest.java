package info.mackiewicz.bankapp.presentation.dashboard.dto;

import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.shared.annotations.DifferentAccounts;
import info.mackiewicz.bankapp.shared.annotations.ValidIban;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@DifferentAccounts
public class ExternalTransferRequest implements WebTransferRequest {
    @ValidIban
    @NotNull(message = "Source IBAN is required")
    private String sourceIban;

    @ValidIban
    @NotEmpty(message = "Recipient IBAN is required")
    @Size(min = 15, max = 34, message = "IBAN must be between 15 and 34 characters")
    private String recipientIban;

    @NotEmpty(message = "Recipient name is required")
    @Size(max = 100, message = "Recipient name cannot exceed 100 characters")
    private String recipientName;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private String amount;

    @NotEmpty(message = "Title is required")
    @Size(max = 140, message = "Title cannot exceed 140 characters")
    private String title;

    private TransactionType transactionType = TransactionType.TRANSFER_EXTERNAL;
}