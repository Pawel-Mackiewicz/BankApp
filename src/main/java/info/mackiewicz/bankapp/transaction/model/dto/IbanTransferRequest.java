package info.mackiewicz.bankapp.transaction.model.dto;

import org.iban4j.Iban;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class IbanTransferRequest extends BankingOperationRequest {

    @Schema(description = "The destination IBAN for the transfer", requiredMode = RequiredMode.REQUIRED) //is requireMode needed?
    @NotNull
    private Iban recipientIban;
}
