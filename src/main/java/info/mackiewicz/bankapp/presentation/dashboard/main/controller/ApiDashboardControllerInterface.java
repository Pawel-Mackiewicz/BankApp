package info.mackiewicz.bankapp.presentation.dashboard.main.controller;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.presentation.dashboard.main.controller.dto.WorkingBalanceResponse;
import info.mackiewicz.bankapp.system.error.handling.dto.BaseApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Interface defining dashboard API endpoints.
 * Provides contract for account information and user dashboard operations.
 */
@Tag(name = "User Dashboard", description = "API for managing user dashboard information such as balance")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/dashboard")
public interface ApiDashboardControllerInterface {

    /**
     * Retrieves the working balance for a specific account
     *
     * @param accountId identifier of the account to get the balance for
     *
     * @return response with working balance information
     */
    @Operation(
            summary = "Get account working balance",
            description = "Returns the current working balance for the specified account. Working balance represents the funds available for use, taking into account any holds placed for pending transactions."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Balance retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WorkingBalanceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not own the account or account has not been found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseApiError.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Account ownership error",
                                            summary = "Example of an account ownership error response",
                                            value = """
                                                    {
                                                      "path": "/api/dashboard/account/123/balance/working",
                                                      "status": "FORBIDDEN",
                                                      "title": "ACCOUNT_OWNERSHIP_ERROR",
                                                      "message": "You do not have permission to access this account.",
                                                      "timestamp": "04-04-2025 13:35:07"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during request processing",
                    content = @Content
            )
    })
    @GetMapping("/account/{accountId}/balance/working")
    ResponseEntity<WorkingBalanceResponse> getWorkingBalance(
            @Parameter(
                    description = "Account identifier",
                    required = true,
                    example = "12345"
            )
            @Min(1) @NotNull @PathVariable Integer accountId, @NotNull @AuthenticationPrincipal User owner);
}
