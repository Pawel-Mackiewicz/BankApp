package info.mackiewicz.bankapp.presentation.dashboard.dto;

import info.mackiewicz.bankapp.shared.annotations.Password;
import info.mackiewicz.bankapp.shared.annotations.PasswordConfirmation;
import info.mackiewicz.bankapp.shared.annotations.PasswordMatches;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@PasswordMatches
public class ChangePasswordRequest implements PasswordConfirmation {
    @NotBlank
    private String currentPassword;

    @Schema(description = "Password must be at least 8 characters long, contain at least one digit, " +
            "one lowercase letter, one uppercase letter, and one special character from the set: @$!%*?&", minLength = 8, pattern = ValidationConstants.PASSWORD_PATTERN, example = "StrongP@ss123")
    @Password
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Password confirmation must match the password", example = "StrongP@ss123")
    private String confirmPassword;
}