package info.mackiewicz.bankapp.system.banking.operations.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.shared.annotations.ValidIban;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.iban4j.Iban;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
@ToString
public abstract class TransactionRequest {
    /**
     * Temporary ID for the transaction, used for tracking purposes.
     * This ID is generated based on the current time in milliseconds.
     */
    @Schema(description = "Temporary ID for the transaction", hidden = true, requiredMode = RequiredMode.NOT_REQUIRED)
    private final Long tempId;

    @Schema(description = "IBAN of the source account", example = "PL11485112340000123400000077")
    @NotBlank(message = "Source IBAN cannot be blank")
    @ValidIban
    @JsonProperty("sourceIban")
    private String sourceIban;

    @Schema(description = "Amount to be transferred", minimum = "0.01")
    @Positive(message = "Amount must be positive")
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @Pattern(regexp = ValidationConstants.TRANSFER_TITLE_PATTERN, message = "Title contains invalid characters")
    @Schema(description = "Title of the transaction")
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @JsonIgnore
    public Iban getSourceIban() {
        return Iban.valueOf(sourceIban);
    }

    @JsonIgnore
    public void setSourceIban(Iban iban) {
        this.sourceIban = iban.toString();
    }

    public void setSourceIban(String iban) {
        this.sourceIban = iban;
    }

    public TransactionRequest() {
        tempId = ChronoUnit.MILLIS.between(
            LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS),
            LocalDateTime.now()
        );
    }
}
