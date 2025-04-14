package info.mackiewicz.bankapp.system.registration.dto;

import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Registration request DTO for demo users")
public class DemoRegistrationRequest {

    @NotBlank(message = "Email is required")
    @Pattern(regexp = ValidationConstants.EMAIL_PATTERN, message = "Invalid email format")
    @Schema(description = "Email must be a valid email address", example = "john.smith@example.com")
    private String email;

    public EmailAddress getEmail() {
        return new EmailAddress(email);
    }
}
