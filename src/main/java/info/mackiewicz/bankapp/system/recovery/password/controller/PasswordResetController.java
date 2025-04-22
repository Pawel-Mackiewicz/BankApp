package info.mackiewicz.bankapp.system.recovery.password.controller;

import info.mackiewicz.bankapp.shared.annotations.ValidEmail;
import info.mackiewicz.bankapp.shared.web.dto.BaseApiError;
import info.mackiewicz.bankapp.shared.web.dto.ValidationApiError;
import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordResetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
@RequestMapping("/api/password")
public interface PasswordResetController {

    @Operation(
            summary = "Initiate password reset process",
            description = """
                    Starts the password reset flow by accepting a user's email address.
                    
                    If a syntactically valid email address is provided, an email containing a password reset link *may* be sent to that address.
                    
                    **Security Note:** To prevent email address enumeration, this endpoint **always returns a 200 OK** response, regardless of whether the provided email address exists in the system.<br>
                    A 200 OK response indicates the request was processed successfully, but does not confirm if the email address is registered or if an email was actually dispatched.
                    
                    Provided email must pass this pattern: 
                    ```
                    ^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$
                    ```
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Request processed successfully. 
                            If the provided email address is registered and syntactically valid, 
                            a password reset link *may* be sent. 
                            (Note: Always returns 200 OK for security reasons, regardless of email existence.)
                            """
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request: The provided email address is either missing or syntactically incorrect.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationApiError.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Error"
            )
    })
    @PostMapping("/reset-request")
    public ResponseEntity<Void> requestReset(@ValidEmail String email);

    @Operation(
            summary = "Complete the password reset process",
            description = """
                    Finalizes the password reset initiated via the `/reset-request` endpoint. 
                    
                    This endpoint requires a valid password reset token (obtained from the reset email link) and the user's desired new password, submitted within the request body (`PasswordResetRequest`).
                    
                    If the token is valid and the new password meets the security requirements, the user's password will be updated successfully.<br>
                    An email notification confirming the password change will then be sent to the user's email address.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successfully completed."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (e.g., password mismatch, weak password, missing fields).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationApiError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "TOKEN_NOT_FOUND: Reset link could not be found or has expired. Please request a new reset link.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))
            )
    })
    @PostMapping("/reset-complete")
    ResponseEntity<Void> completeReset(@Valid @RequestBody PasswordResetRequest request);
}
