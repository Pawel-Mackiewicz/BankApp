package info.mackiewicz.bankapp.transaction.model.dto;

import org.iban4j.Iban;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class IbanTransferRequest extends BankingOperationRequest {

    @Schema(description = "The destination IBAN for the transfer")
    @NotNull
    private Iban destinationIban;
}
