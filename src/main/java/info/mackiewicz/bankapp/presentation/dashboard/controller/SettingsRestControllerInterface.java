package info.mackiewicz.bankapp.presentation.dashboard.controller;

import org.springframework.http.ResponseEntity;

import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.UserSettingsDTO;
import info.mackiewicz.bankapp.shared.web.dto.BaseApiError;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.interfaces.PersonalInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Interface defining REST API endpoints for user settings management.
 * <p>
 * This controller interface provides operations for retrieving and updating user settings,
 * including password and username changes. All operations require authentication and
 * the user is automatically taken from the security context using @AuthenticationPrincipal
 * annotation in the implementation.
 * <p>
 * Note: Authentication is session-based. The user must be logged in to access these endpoints.
 * Spring Security will automatically provide the authenticated user object to the methods via
 * the @AuthenticationPrincipal annotation in the controller implementation.
 */
@SecurityRequirement(name = "cookieAuth")
public interface SettingsRestControllerInterface {

    @Operation(
        summary = "Get user settings", 
        description = " Retrieves the current settings for the authenticated user. The user information is automatically " +
                     "extracted from the current session. You must be logged in to access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Settings retrieved successfully", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSettingsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access - user is not logged in",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class)))
    })
    ResponseEntity<UserSettingsDTO> getUserSettings(
            @Parameter(hidden = true, description = "Current authenticated user (automatically injected by Spring Security)") 
            PersonalInfo user);

    @Operation(
        summary = "Change user password", 
        description = "Changes the password for the currently logged in user. After successful change, user will be logged out. " +
                     "The user is automatically identified from the current session."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, 
        description = "Password change details", 
        content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = ChangePasswordRequest.class),
            examples = {
                @ExampleObject(name = "Password change request", value = """
                    {
                      "currentPassword": "oldP@ssword123",
                      "newPassword": "newSecurePassword456!",
                      "confirmPassword": "newSecurePassword456!"
                    }
                    """
                )
            }))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password data",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class),
                examples = {
                    @ExampleObject(name = "Invalid password response", value = """
                        {
                          "status": "BAD_REQUEST",
                          "title": "INVALID_PASSWORD",
                          "message": "Current password is incorrect",
                          "path": "/api/settings/change-password",
                          "timestamp": "28-03-2025 14:30:45"
                        }
                        """
                    )
                })),
            @ApiResponse(responseCode = "401", description = "Unauthorized access - user is not logged in")
    })
    ResponseEntity<?> changePassword(
            @Parameter(hidden = true, description = "Current authenticated user (automatically injected by Spring Security)") 
            User user, 
            ChangePasswordRequest request, 
            HttpServletRequest httpRequest);

    @Operation(
        summary = "Change username", 
        description = "Changes the username for the currently logged in user. The user is automatically identified from the current session."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
        description = "Username change details",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ChangeUsernameRequest.class),
            examples = {
                @ExampleObject(name = "Username change request", value = """
                    {
                      "newUsername": "newUsername123"
                    }
                    """
                )
            }))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid username", 
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class),
                examples = {
                    @ExampleObject(name = "Invalid username response", value = """
                        {
                          "status": "BAD_REQUEST",
                          "title": "INVALID_USERNAME",
                          "message": "Username already taken",
                          "path": "/api/settings/change-username",
                          "timestamp": "28-03-2025 14:30:45"
                        }
                        """
                    )
                })),
            @ApiResponse(responseCode = "401", description = "Unauthorized access - user is not logged in")
    })
    ResponseEntity<Void> changeUsername(
            @Parameter(hidden = true, description = "Current authenticated user (automatically injected by Spring Security)") 
            User user, 
            ChangeUsernameRequest request);
}