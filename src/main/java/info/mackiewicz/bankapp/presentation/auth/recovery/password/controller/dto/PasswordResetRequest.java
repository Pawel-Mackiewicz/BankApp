package info.mackiewicz.bankapp.presentation.auth.recovery.password.controller.dto;

import info.mackiewicz.bankapp.shared.annotations.ValidEmail;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for password reset request
 */
@Getter
@Setter
public class PasswordResetRequest {

    @ValidEmail
    @Schema(pattern = ValidationConstants.EMAIL_PATTERN)
    private String email;
}