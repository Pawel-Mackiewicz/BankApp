package info.mackiewicz.bankapp.system.banking.api.dto;


import org.iban4j.Iban;

import info.mackiewicz.bankapp.account.validation.DifferentAccounts;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@DifferentAccounts
@Getter
@Setter
@Schema(description = "Request object for transferring money to an IBAN account")
public class IbanTransferRequest extends BankingOperationRequest {

    @Schema(description = "IBAN of the source account", requiredMode = RequiredMode.REQUIRED, example = "PL99485112340000123400000099", type = "string")
    @NotNull
    private Iban recipientIban;
}
