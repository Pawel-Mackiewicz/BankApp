package info.mackiewicz.bankapp.presentation.dashboard.dto;

import info.mackiewicz.bankapp.account.validation.Iban;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OwnTransferRequest implements TransferRequest {

    // This field is not used in the application yet
    @Iban
    private String sourceIban;
    // This field is not used in the application yet
    @Iban
    private String recipientIban;

    @NotNull(message = "Source account is required")
    private Integer sourceAccountId;

    @NotNull(message = "Destination account is required")
    private Integer destinationAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private String amount;

    @NotEmpty(message = "Title is required")
    private String title;

    private TransactionType transactionType = TransactionType.TRANSFER_OWN;

}