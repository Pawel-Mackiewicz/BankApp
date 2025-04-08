package info.mackiewicz.bankapp.system.banking.api.dto;

import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import info.mackiewicz.bankapp.user.model.vo.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "Request object for transferring money to an email address")
@ToString(callSuper = true)
@Setter
public class EmailTransferRequest extends BankingOperationRequest {

    @Schema(description = "The destination email address for the transfer", requiredMode = RequiredMode.REQUIRED, example = "example@user.com")
    @Pattern(regexp = ValidationConstants.EMAIL_PATTERN, message = "Invalid email format")
    @NotBlank
    private String destinationEmail;

    /**
     * Returns an Email object created from the destination email address.
     *
     * <p>This method instantiates a new Email using the destinationEmail field, providing a typed representation
     * of the email address for banking operations.</p>
     *
     * @return a new Email instance initialized with the current destination email address
     */
    public Email getDestinationEmail() {
        return new Email(destinationEmail);
    }

    /**
     * Sets the destination email address based on the provided Email object.
     *
     * This method extracts the string value from the Email instance and assigns it
     * to the destination email field.
     *
     * @param email the Email instance containing the destination email address
     */
    public void setDestinationEmail(Email email) {
        this.destinationEmail = email.getValue();
    }
}
