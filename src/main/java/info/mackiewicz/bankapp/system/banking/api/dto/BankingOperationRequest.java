package info.mackiewicz.bankapp.system.banking.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.iban4j.Iban;

import info.mackiewicz.bankapp.account.validation.ValidIban;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * Class representing a banking operation request.
 * This class serves as a base for various banking operations such as transfers.
 * It can also be used for withdrawals and deposits.
 * It will be converted to Transaction.java objects
 * 
 * @see Transaction
 */
@Getter
@Setter
public abstract class BankingOperationRequest {

    private final LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "IBAN of the source account", requiredMode = RequiredMode.REQUIRED, example = "PL11485112340000123400000077")
    @NotBlank(message = "Source IBAN cannot be blank")
    @ValidIban(message = "Invalid IBAN format")
    private String sourceIban;

    @Schema(description = "Amount to be transferred", required = true, minimum = "0.01")
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Schema(description = "Title of the transaction", required = false)
    @NotBlank(message = "Title cannot be blank")
    private String title;

    public Iban getSourceIban() {
        return Iban.valueOf(sourceIban);
    }

    public void setSourceIban(Iban iban) {
        this.sourceIban = iban.toString();
    }

    public void setSourceIban(String iban) {
        this.sourceIban = iban;
    }
}
