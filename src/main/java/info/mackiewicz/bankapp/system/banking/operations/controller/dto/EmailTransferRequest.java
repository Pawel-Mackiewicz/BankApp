package info.mackiewicz.bankapp.system.banking.operations.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "Request object for transferring money to an email address")
@ToString(callSuper = true)
@Setter
public class EmailTransferRequest extends TransactionRequest {

    @Schema(description = "The destination email address for the transfer", requiredMode = RequiredMode.REQUIRED, example = "example@user.com")
    @Pattern(regexp = ValidationConstants.EMAIL_PATTERN, message = "Invalid email format")
    @NotBlank
    @JsonProperty("destinationEmail")
    private String destinationEmail;

    @JsonIgnore
    public EmailAddress getDestinationEmail() {
        return new EmailAddress(destinationEmail);
    }

    public void setDestinationEmail(EmailAddress email) {
        this.destinationEmail = email.getValue();
    }
}
