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
     * Returns a new Email object constructed from the destination email string.
     *
     * @return a new Email instance initialized with the value of destinationEmail
     */
    public Email getDestinationEmail() {
        return new Email(destinationEmail);
    }

    /**
     * Sets the destination email for this transfer request.
     *
     * <p>The Email object's value is extracted and assigned to the destination email field.</p>
     *
     * @param email the Email object containing the destination email address
     */
    public void setDestinationEmail(Email email) {
        this.destinationEmail = email.getValue();
    }
}
