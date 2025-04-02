package info.mackiewicz.bankapp.transaction.model.dto;

import java.math.BigDecimal;

import org.iban4j.Iban;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
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
 @see Transaction
 */
@Getter
@Setter
public abstract class BankingOperationRequest {

    @Schema(description = "IBAN of the source account", required = true)
    @NotNull
    private Iban sourceIban;
    
    @Schema(description = "Amount to be transferred", required = true)
    @NotNull
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @Schema(description = "Title of the transaction", required = false)
    @NotBlank(message = "Title cannot be blank")
    private String title;

}
