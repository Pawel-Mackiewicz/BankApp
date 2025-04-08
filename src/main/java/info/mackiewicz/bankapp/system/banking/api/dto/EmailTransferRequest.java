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
     * Returns a new Email instance using the stored destination email address.
     *
     * <p>This method creates an Email object from the destinationEmail field, providing a
     * strongly-typed representation for email transfer operations.
     *
     * @return an Email object representing the destination email address
     */
    public Email getDestinationEmail() {
        return new Email(destinationEmail);
    }

    /**
     * Sets the destination email address for this transfer request.
     *
     * <p>This method assigns the email's string value to the destination email field,
     * which is used to identify the recipient of the transfer.</p>
     *
     * @param email the Email object containing the new destination address
     */
    public void setDestinationEmail(Email email) {
        this.destinationEmail = email.getValue();
    }
}
