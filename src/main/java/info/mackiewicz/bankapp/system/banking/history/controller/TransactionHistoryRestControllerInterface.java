package info.mackiewicz.bankapp.system.banking.history.controller;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.system.banking.history.controller.dto.TransactionFilterRequest;
import info.mackiewicz.bankapp.system.banking.shared.dto.TransactionResponse;
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
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@Tag(name = "Transaction History", description = "Operations for managing and exporting banking transaction history")
@RequestMapping("/api/banking/history")
public interface TransactionHistoryRestControllerInterface {

    @Operation(
            summary = "Get filtered transactions",
            description = """
                    Retrieves a paginated list of transactions for a specific account with optional filtering criteria.
                    
                    User information is automatically extracted from the current session.
                    
                    You must be logged in to access this endpoint."""
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageTransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - Account doesn't belong to user",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access - user is not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))
            )
    })
    @GetMapping
    ResponseEntity<Page<TransactionResponse>> getTransactions(
            @Parameter(
                    hidden = true,
                    description = "Current authenticated user (automatically injected by Spring Security)"
            ) User user,
            @Parameter(
                    description = "Transaction filter options",
                    schema = @Schema(implementation = TransactionFilterRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Basic Filtering",
                                    summary = "Example of basic filtering",
                                    value = "{\"accountId\":23, \"page\":0, \"size\":10}"
                            ),
                            @ExampleObject(
                                    name = "Advanced Filtering",
                                    summary = "Filtering with all available parameters",
                                    value = "{\"accountId\":23, \"amountFrom\":100.00, \"amountTo\":500.00, \"dateFrom\":\"2025-01-01T00:00:00\", \"dateTo\":\"2025-03-30T23:59:59\", \"type\":\"TRANSFER_OWN\", \"sortDirection\":\"DESCENDING\", \"sortBy\":\"date\", \"page\":0, \"size\":10, \"query\":\"Store\"}"
                            )
                    }
            ) @Valid TransactionFilterRequest filter);

    @Operation(
            summary = "Export filtered transactions",
            description = """
                    Exports transactions for a specific account in the requested format (default: CSV).<br>
                    Supports the same filtering criteria as the GET /history endpoint.
                    
                    User information is automatically extracted from the current session.
                    
                    You must be logged in to access this endpoint.
                    
                    
                    **NOTE: EXCEPTION HANDLING NOT SUPPORTED. IF ERRORED WILL ALWAYS THROW 500.**"""
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions exported successfully",
                    content = @Content(mediaType = "application/octet-stream")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized access - user is not logged in",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Bad Request - Export format not supported."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unsupported export format"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Access denied - Account doesn't belong to user"
            )
    })
    @GetMapping("/export")
    ResponseEntity<byte[]> exportTransactions(
            @Parameter(
                    hidden = true,
                    description = "Current authenticated user (automatically injected by Spring Security)"
            ) User user,
            @Parameter(
                    description = "Transaction filter options",
                    schema = @Schema(implementation = TransactionFilterRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Basic Export Filtering",
                                    summary = "Example of basic filtering for export",
                                    value = "{\"accountId\":123, \"page\":0, \"size\":100}"
                            ),
                            @ExampleObject(
                                    name = "Advanced Export Filtering",
                                    summary = "Filtering with all available parameters for export",
                                    value = "{\"accountId\":123, \"amountFrom\":100.00, \"amountTo\":500.00, \"dateFrom\":\"2025-01-01T00:00:00\", \"dateTo\":\"2025-03-30T23:59:59\", \"type\":\"TRANSFER_OWN\", \"sortDirection\":\"DESCENDING\", \"sortBy\":\"date\", \"page\":0, \"size\":100, \"query\":\"Store\"}"
                            )
                    }
            ) @Valid TransactionFilterRequest filter,
            @Parameter(
                    description = "Export format (supported formats: csv, pdf)",
                    examples = {
                            @ExampleObject(value = "csv"),
                            @ExampleObject(value = "pdf")
                    }
            ) @RequestParam(name = "format", defaultValue = "csv") String format);
}