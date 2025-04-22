package info.mackiewicz.bankapp.system.recovery.password.controller;

import info.mackiewicz.bankapp.shared.annotations.ValidEmail;
import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordResetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PasswordResetController defines the endpoints for managing password reset functionality.
 * This interface is responsible for handling user requests to initiate and complete the
 * password reset process.
 * <p>
 * It includes operations for:
 * - Requesting a password reset by providing an email address.
 * - Completing the password reset by submitting a token and new password.
 */
@Tag(name = "Password Reset")
@RestController
@RequestMapping("/api/password")
public interface PasswordResetController {

    @Operation(
            summary = "Post email address for which you want to reset password",
            description = "If valid email address is provided - You will get 200 response. Even when this email address isn't part of our database"
    )
    @PostMapping("/reset-request")
    public ResponseEntity<Void> requestReset(@ValidEmail String email);

    @Operation(
            summary = "Post Pa"
    )
    @PostMapping("/reset-complete")
    ResponseEntity<Void> completeReset(@Valid PasswordResetRequest request);

}
