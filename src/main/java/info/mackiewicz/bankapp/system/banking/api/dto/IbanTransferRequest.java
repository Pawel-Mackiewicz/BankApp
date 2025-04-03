package info.mackiewicz.bankapp.system.banking.api.dto;


import org.iban4j.Iban;

import info.mackiewicz.bankapp.account.validation.ValidIban;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for transferring money to an IBAN account")
public class IbanTransferRequest extends BankingOperationRequest {

    @Schema(description = "IBAN of the source account", requiredMode = RequiredMode.REQUIRED, example = "PL99485112340000123400000099")
    @NotBlank(message = "Source IBAN cannot be blank")
    @ValidIban(message = "Invalid IBAN format")
    private String recipientIban;

    public Iban getRecipientIban() {
        return Iban.valueOf(recipientIban);
    }

    public void setRecipientIban(Iban iban) {
        this.recipientIban = iban.toString();
    }

    public void setRecipientIban(String iban) {
        this.recipientIban = iban;
    }
}
