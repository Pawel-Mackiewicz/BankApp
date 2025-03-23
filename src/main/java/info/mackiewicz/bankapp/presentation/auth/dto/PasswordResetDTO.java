package info.mackiewicz.bankapp.presentation.auth.dto;

import info.mackiewicz.bankapp.presentation.auth.validation.Password;
import info.mackiewicz.bankapp.presentation.auth.validation.PasswordMatches;
import info.mackiewicz.bankapp.shared.web.dto.interfaces.PasswordConfirmation;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for password reset
 */
@Data
@PasswordMatches
public class PasswordResetDTO implements PasswordConfirmation {

    @NotBlank(message = "Token is required")
    private String token;
    
    @Password
    private String password;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}