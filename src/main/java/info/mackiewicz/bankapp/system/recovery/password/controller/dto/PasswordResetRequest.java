package info.mackiewicz.bankapp.system.recovery.password.controller.dto;

import info.mackiewicz.bankapp.shared.annotations.Password;
import info.mackiewicz.bankapp.shared.annotations.PasswordMatches;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import info.mackiewicz.bankapp.shared.web.dto.interfaces.PasswordConfirmation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for password reset
 */
@Getter
@Setter
@PasswordMatches
public class PasswordResetRequest implements PasswordConfirmation {

    @NotBlank(message = "Token is required")
    private String token;

    @Schema(
            description = "Password must be at least 8 characters long, contain at least one digit, " +
                    "one lowercase letter, one uppercase letter, and one special character from the set: @$!%*?&",
            minLength = 8,
            pattern = ValidationConstants.PASSWORD_PATTERN,
            example = "StrongP@ss123"
    )
    @Password
    private String password;

    @Schema(description = "Password confirmation must match the password", example = "StrongP@ss123")
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}