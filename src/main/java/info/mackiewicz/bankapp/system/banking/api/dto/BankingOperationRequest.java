package info.mackiewicz.bankapp.system.banking.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
import lombok.ToString;

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
public abstract class BankingOperationRequest {
    /**
     * Temporary ID for the transaction, used for tracking purposes.
     * This ID is generated based on the current time in milliseconds.
     */
    @Schema(description = "Temporary ID for the transaction", hidden = true, requiredMode = RequiredMode.NOT_REQUIRED)
    private final Long tempId;

    @Schema(description = "IBAN of the source account", example = "PL11485112340000123400000077")
    @NotBlank(message = "Source IBAN cannot be blank")
    @ValidIban
    private String sourceIban;

    @Schema(description = "Amount to be transferred", minimum = "0.01")
    @Positive(message = "Amount must be positive")
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @Schema(description = "Title of the transaction")
    @NotBlank(message = "Title cannot be blank")
    private String title;

    /**
     * Returns the source IBAN as an Iban object.
     *
     * <p>This method converts the string representation of the source IBAN into an Iban instance.
     * It is intended to provide a standard object format for further processing in banking operations.</p>
     *
     * @return an Iban object representing the source IBAN
     */
    public Iban getSourceIban() {
        return Iban.valueOf(sourceIban);
    }

    /**
     * Sets the source IBAN for the banking operation using the provided Iban object.
     *
     * <p>This method converts the Iban object to its string representation and assigns it to the sourceIban field.
     *
     * @param iban the Iban object representing the source account's IBAN
     */
    public void setSourceIban(Iban iban) {
        this.sourceIban = iban.toString();
    }

    /**
     * Sets the source account IBAN for the banking operation.
     *
     * @param iban a valid IBAN string to assign to the source account
     */
    public void setSourceIban(String iban) {
        this.sourceIban = iban;
    }

    /**
     * Constructs a new BankingOperationRequest with a temporary ID.
     *
     * <p>The temporary ID (tempId) is set to the number of milliseconds that have elapsed
     * from the start of the current month (with the time truncated to the beginning of the day)
     * until the current moment. This value serves as a unique identifier for the transaction.</p>
     */
    public BankingOperationRequest() {
        tempId = ChronoUnit.MILLIS.between(
            LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS),
            LocalDateTime.now()
        );
    }
}
