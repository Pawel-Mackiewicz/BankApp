package info.mackiewicz.bankapp.presentation.dashboard.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import info.mackiewicz.bankapp.presentation.dashboard.dto.TransactionFilterDTO;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

/**
 * Interface defining REST API endpoints for transaction history management.
 * <p>
 * This controller interface provides operations for retrieving and exporting transaction history
 * with various filtering and sorting options. All operations require authentication and
 * the user is automatically taken from the security context using @AuthenticationPrincipal
 * annotation in the implementation.
 * <p>
 * Note: Authentication is session-based. The user must be logged in to access these endpoints.
 * Spring Security will automatically provide the authenticated user object to the methods via
 * the @AuthenticationPrincipal annotation in the controller implementation.
 */
@SecurityRequirement(name = "cookieAuth")
public interface TransactionHistoryRestControllerInterface {

    static final String NOT_IMPLEMENTED_YET = "NOT ALL API RESPONSES ARE IMPLEMENTED YET.\n";

    @Operation(summary = "Get filtered transactions", description = "Retrieves a paginated list of transactions for a specific account with optional filtering criteria. The user information is automatically extracted from the current session. You must be logged in to access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = NOT_IMPLEMENTED_YET + "Access denied - Account doesn't belong to user", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = NOT_IMPLEMENTED_YET + "Account not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = NOT_IMPLEMENTED_YET + "Unauthorized access - user is not logged in", content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<Page<Transaction>> getTransactions(
            @Parameter(hidden = true, description = "Current authenticated user (automatically injected by Spring Security)") User user,
            @Parameter(description = "Transaction filter options", 
                schema = @Schema(implementation = TransactionFilterDTO.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"accountId\":\"123e4567-e89b-12d3-a456-426614174000\", \"minAmount\":\"100.00\", \"maxAmount\":\"500.00\", \"startDate\":\"2025-01-01\", \"endDate\":\"2025-03-30\", \"transactionTypes\":[\"TRANSFER\", \"PAYMENT\"], \"sortDirection\":\"DESC\", \"sortBy\":\"date\", \"page\":0, \"size\":10}"
                )
            ) @Valid TransactionFilterDTO filter);

    @Operation(summary = "Export filtered transactions", description = "Export transactions for a specific account in the requested format (default: CSV). Supports the same filtering criteria as the get transactions endpoint. The user information is automatically extracted from the current session. You must be logged in to access this endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions exported successfully", content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "403", description = NOT_IMPLEMENTED_YET + "Access denied - Account doesn't belong to user", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = NOT_IMPLEMENTED_YET + "Account not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = NOT_IMPLEMENTED_YET + "Unsupported export format", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = NOT_IMPLEMENTED_YET + "Unauthorized access - user is not logged in", content = @Content(mediaType = "application/json"))
    })
    ResponseEntity<byte[]> exportTransactions(
            @Parameter(hidden = true, description = "Current authenticated user (automatically injected by Spring Security)") User user,
            @Parameter(description = "Transaction filter options", 
                schema = @Schema(implementation = TransactionFilterDTO.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"accountId\":\"123e4567-e89b-12d3-a456-426614174000\", \"minAmount\":\"100.00\", \"maxAmount\":\"500.00\", \"startDate\":\"2025-01-01\", \"endDate\":\"2025-03-30\", \"transactionTypes\":[\"TRANSFER\", \"PAYMENT\"], \"sortDirection\":\"DESC\", \"sortBy\":\"date\", \"page\":0, \"size\":10}"
                )
            ) @Valid TransactionFilterDTO filter,
            @Parameter(description = "Export format (supported formats: csv, pdf)", example = "csv") String format);

}