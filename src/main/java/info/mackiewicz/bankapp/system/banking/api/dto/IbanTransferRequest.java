package info.mackiewicz.bankapp.system.banking.api.dto;


import org.iban4j.Iban;

import info.mackiewicz.bankapp.account.validation.ValidIban;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "Request object for transferring money to an IBAN account")
@ToString(callSuper = true)
@Setter
public class IbanTransferRequest extends BankingOperationRequest {

    @Schema(description = "IBAN of the source account", example = "PL99485112340000123400000099")
    @NotBlank(message = "Source IBAN cannot be blank")
    @ValidIban
    private String recipientIban;

    public Iban getRecipientIban() {
        return Iban.valueOf(recipientIban);
    }

    public void setRecipientIban(Iban iban) {
        this.recipientIban = iban.toString();
    }
}
