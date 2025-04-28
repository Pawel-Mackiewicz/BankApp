package info.mackiewicz.bankapp.presentation.dashboard.controller;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.interfaces.PersonalInfo;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.UserSettingsDTO;
import info.mackiewicz.bankapp.system.error.handling.dto.BaseApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 * Interface defining REST API endpoints for user settings management.
 * <p>
 * This controller interface provides operations for retrieving and updating
 * user settings,
 * including password and username changes. All operations require
 * authentication and
 * the user is automatically taken from the security context
 * using @AuthenticationPrincipal
 * annotation in the implementation.
 * <p>
 * Note: Authentication is session-based. The user must be logged in to access
 * these endpoints.
 * Spring Security will automatically provide the authenticated user object to
 * the methods via
 * the @AuthenticationPrincipal annotation in the controller implementation.
 */
@SecurityRequirement(name = "cookieAuth")
public interface SettingsRestControllerInterface {

        @Operation(summary = "Get user settings", description = " Retrieves the current settings for the authenticated user. The user information is automatically "
                        +
                        "extracted from the current session. You must be logged in to access this endpoint.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Settings retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSettingsDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access - user is not logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class)))
        })
        ResponseEntity<UserSettingsDTO> getUserSettings(
                        @Parameter(hidden = true, description = "Current authenticated user (automatically injected by Spring Security)") PersonalInfo user);

        @Operation(summary = "Change user password", description = "Changes the password for the currently logged in user. After successful change, user will be logged out. "  +
                        "The user is automatically identified from the current session.")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Password change details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangePasswordRequest.class), examples = {
                        @ExampleObject(name = "Password change request", value = """
                                        {
                                          "currentPassword": "oldP@ssword123",
                                          "password": "newSecurePassword456!",
                                          "confirmPassword": "newSecurePassword456!"
                                        }
                                        """)
        }))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid password data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class), examples = {
                                        @ExampleObject(name = "Invalid current password", value = """
                                                        {
                                                          "status": "BAD_REQUEST",
                                                          "title": "INVALID_PASSWORD",
                                                          "message": "The provided password is invalid. Please check your input and try again.",
                                                          "path": "/api/settings/change-password",
                                                          "timestamp": "30-03-2025 15:06:32"
                                                        }
                                                        """),
                                        @ExampleObject(name = "Password confirmation mismatch", value = """
                                                        {
                                                          "path": "/api/settings/change-password",
                                                          "errors": [
                                                            {
                                                              "field": "confirmPassword",
                                                              "message": "Passwords do not match",
                                                              "rejectedValue": "newSecurePassword456"
                                                            }
                                                          ],
                                                          "status": "BAD_REQUEST",
                                                          "title": "VALIDATION_ERROR",
                                                          "message": "Validation failed. Please check your input and try again.",
                                                          "timestamp": "30-03-2025 15:08:22"
                                                        }
                                                        """),
                                        @ExampleObject(name = "Password same as current", value = """
                                                        {
                                                          "status": "BAD_REQUEST",
                                                          "title": "PASSWORD_SAME",
                                                          "message": "New password cannot be the same as the current one.",
                                                          "path": "/api/settings/change-password",
                                                          "timestamp": "30-03-2025 15:08:12"
                                                        }
                                                        """),
                                        @ExampleObject(name = "Password too weak", value = """
                                                        {
                                                          "path": "/api/settings/change-password",
                                                          "errors": [
                                                            {
                                                              "field": "password",
                                                              "message": "Password must contain at least one digit, one lowercase, one uppercase letter and one special character (@$!%*?&)",
                                                              "rejectedValue": "newSecurePassword456"
                                                            }
                                                          ],
                                                          "status": "BAD_REQUEST",
                                                          "title": "VALIDATION_ERROR",
                                                          "message": "Validation failed. Please check your input and try again.",
                                                          "timestamp": "30-03-2025 15:09:17"
                                                        }
                                                        """)
                        })),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access - user is not logged in")
        })
        ResponseEntity<?> changePassword(
                        @Parameter(hidden = true, description = "Current authenticated user (automatically injected by Spring Security)") User user,
                        ChangePasswordRequest request,
                        HttpServletRequest httpRequest);

        @Operation(summary = "Change username", description = "Changes the username for the currently logged in user. The user is automatically identified from the current session.")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Username change details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangeUsernameRequest.class), examples = {
                        @ExampleObject(name = "Username change request", value = """
                                        {
                                          "newUsername": "newUsername123"
                                        }
                                        """)
        }))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Username changed successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid username", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class), examples = {
                                        @ExampleObject(name = "username already taken", value = """
                                                        {
                                                            "status": "CONFLICT",
                                                            "title": "USERNAME_TAKEN",
                                                            "message": "Username is already taken. Please choose a different one.",
                                                            "path": "/api/settings/change-username",
                                                            "timestamp": "30-03-2025 14:53:56"
                                                          }
                                                          """),
                                        @ExampleObject(name = "username same as current", value = """
                                                        {
                                                          "status": "BAD_REQUEST",
                                                          "title": "USERNAME_SAME",
                                                          "message": "New username cannot be the same as the current one.",
                                                          "path": "/api/settings/change-username",
                                                          "timestamp": "30-03-2025 14:51:47"
                                                        }
                                                        """),
                                        @ExampleObject(name = "forbidden username", value = """
                                                        {
                                                            "status": "BAD_REQUEST",
                                                            "title": "USERNAME_FORBIDDEN",
                                                            "message": "Username is forbidden. Please choose a different one.",
                                                            "path": "/api/settings/change-username",
                                                            "timestamp": "30-03-2025 15:02:01"
                                                          }
                                                          """)
                        })),
                        @ApiResponse(responseCode = "401", description = "Unauthorized access - user is not logged in")
        })
        ResponseEntity<Void> changeUsername(
                        @Parameter(hidden = true, description = "Current authenticated user (automatically injected by Spring Security)") User user,
                        ChangeUsernameRequest request);
}