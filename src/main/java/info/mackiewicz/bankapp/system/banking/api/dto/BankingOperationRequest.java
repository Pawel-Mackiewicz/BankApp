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
     * <p>This method converts the stored string representation of the source IBAN into an Iban object,
     * enabling further processing that depends on a validated IBAN format.</p>
     *
     * @return the source IBAN in Iban format
     */
    public Iban getSourceIban() {
        return Iban.valueOf(sourceIban);
    }

    /**
     * Sets the source IBAN for the transaction using an Iban object.
     *
     * <p>Converts the provided Iban to its string representation and assigns it to the source IBAN field.</p>
     *
     * @param iban the Iban object representing the source account's IBAN.
     */
    public void setSourceIban(Iban iban) {
        this.sourceIban = iban.toString();
    }

    /**
     * Sets the source IBAN for the banking operation.
     *
     * @param iban the IBAN of the source account as a String
     */
    public void setSourceIban(String iban) {
        this.sourceIban = iban;
    }

    /**
     * Constructs a new BankingOperationRequest and initializes the temporary transaction ID.
     *
     * <p>
     * The temporary ID is calculated as the number of milliseconds between the start of the current month
     * (with the day set to 1 and time truncated to the start of the day) and the current system time.
     * </p>
     */
    public BankingOperationRequest() {
        tempId = ChronoUnit.MILLIS.between(
            LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS),
            LocalDateTime.now()
        );
    }
}
