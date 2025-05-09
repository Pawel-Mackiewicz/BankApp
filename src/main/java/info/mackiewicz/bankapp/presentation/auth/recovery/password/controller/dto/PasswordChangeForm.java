package info.mackiewicz.bankapp.presentation.auth.recovery.password.controller.dto;

import info.mackiewicz.bankapp.shared.annotations.Password;
import info.mackiewicz.bankapp.shared.annotations.PasswordConfirmation;
import info.mackiewicz.bankapp.shared.annotations.PasswordMatches;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
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
public class PasswordChangeForm implements PasswordConfirmation {

    @NotBlank(message = "Token is required")
    private String token;

    @Schema(
            description = ValidationConstants.PASSWORD_DESCRIPTION,
            minLength = ValidationConstants.PASSWORD_MIN_LENGTH,
            pattern = ValidationConstants.PASSWORD_PATTERN,
            example = "StrongP@ss123"
    )
    @Password
    private String password;

    @Schema(description = "Password confirmation must match the password", example = "StrongP@ss123")
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}