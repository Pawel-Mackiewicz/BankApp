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

    @Schema(description = "IBAN of the recipient account", example = "PL99485112340000123400000099")
    @NotBlank(message = "Recipient IBAN cannot be blank")
    @ValidIban
    private String recipientIban;

    /**
     * Retrieves the recipient's IBAN as an Iban object.
     *
     * <p>This method converts the stored string representation of the recipient's IBAN to its corresponding
     * Iban instance using <code>Iban.valueOf</code>, enabling further processing in banking operations.</p>
     *
     * @return an Iban instance representing the recipient's IBAN
     */
    public Iban getRecipientIban() {
        return Iban.valueOf(recipientIban);
    }

    /**
     * Sets the recipient's IBAN by converting the provided Iban object to its string representation.
     *
     * @param iban the Iban object representing the recipient's bank account
     */
    public void setRecipientIban(Iban iban) {
        this.recipientIban = iban.toString();
    }
}
