package info.mackiewicz.bankapp.system.banking.api.dto;

import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import info.mackiewicz.bankapp.user.model.vo.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;

@Setter
public class EmailTransferRequest extends BankingOperationRequest {

    @Schema(description = "The destination email address for the transfer", requiredMode = RequiredMode.REQUIRED, example = "example@user.com")
    @NotBlank
    @Pattern(regexp = ValidationConstants.EMAIL_PATTERN, message = "Invalid email format")
    private String destinationEmail;

    public Email getDestinationEmail() {
        return new Email(destinationEmail);
    }

    public void setDestinationEmail(Email email) {
        this.destinationEmail = email.getValue();
    }
}
