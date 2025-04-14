package info.mackiewicz.bankapp.system.registration.dto;

import info.mackiewicz.bankapp.presentation.auth.validation.Password;
import info.mackiewicz.bankapp.presentation.auth.validation.PasswordMatches;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import info.mackiewicz.bankapp.shared.web.dto.interfaces.PasswordConfirmation;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Registration request DTO for demo users")
@PasswordMatches
public class DemoRegistrationRequest implements PasswordConfirmation {

    @NotBlank(message = "Email is required")
    @Pattern(regexp = ValidationConstants.EMAIL_PATTERN, message = "Invalid email format")
    @Schema(description = "Email must be a valid email address", example = "john.smith@example.com")
    private String email;

    @Password
    @Schema(description = "Password must be at least 8 characters long, contain at least one digit, " +
            "one lowercase letter, one uppercase letter, and one special character from the set: @$!%*?&", minLength = 8, pattern = ValidationConstants.PASSWORD_PATTERN, example = "StrongP@ss123")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Password confirmation must match the password", example = "StrongP@ss123")
    private String confirmPassword;

    public EmailAddress getEmail() {
        return new EmailAddress(email);
    }
}
