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
     * Returns the recipient IBAN as an Iban object.
     *
     * <p>This method converts the stored recipient IBAN string to an Iban instance by invoking {@link Iban#valueOf(String)}.
     *
     * @return an Iban object corresponding to the recipient IBAN
     */
    public Iban getRecipientIban() {
        return Iban.valueOf(recipientIban);
    }

    /**
     * Sets the recipient IBAN field using the string representation of the provided Iban object.
     *
     * @param iban the Iban object to be converted and stored as the recipient IBAN
     */
    public void setRecipientIban(Iban iban) {
        this.recipientIban = iban.toString();
    }
}
