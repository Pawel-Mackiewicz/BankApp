package info.mackiewicz.bankapp.system.banking.api.dto;

import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
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

    public EmailAddress getDestinationEmail() {
        return new EmailAddress(destinationEmail);
    }

    public void setDestinationEmail(EmailAddress email) {
        this.destinationEmail = email.getValue();
    }
}
