package info.mackiewicz.bankapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import info.mackiewicz.bankapp.validation.ValidationConstants;

/**
 * DTO for password reset
 */
@Data
public class PasswordResetDTO {

    @NotBlank(message = "Token is required")
    private String token;
    
    @NotBlank(message = "Password is required")
    @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, message = "Password must be at least " + ValidationConstants.PASSWORD_MIN_LENGTH + " characters long")
    @Pattern(
        regexp = ValidationConstants.PASSWORD_PATTERN,
        message = ValidationConstants.PASSWORD_DESCRIPTION
    )
    private String password;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}