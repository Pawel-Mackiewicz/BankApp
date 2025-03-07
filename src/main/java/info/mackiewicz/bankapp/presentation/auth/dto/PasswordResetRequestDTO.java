package info.mackiewicz.bankapp.presentation.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for password reset request
 */
@Data
public class PasswordResetRequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}