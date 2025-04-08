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
     * Retrieves the recipient IBAN as an Iban object.
     *
     * <p>This method converts the stored IBAN string to its Iban representation.
     *
     * @return the recipient IBAN represented as an Iban object
     */
    public Iban getRecipientIban() {
        return Iban.valueOf(recipientIban);
    }

    /**
     * Sets the recipient IBAN.
     *
     * Converts the provided Iban object into its string representation and assigns it to the recipient IBAN field.
     *
     * @param iban the Iban object representing the recipient's IBAN
     */
    public void setRecipientIban(Iban iban) {
        this.recipientIban = iban.toString();
    }
}
