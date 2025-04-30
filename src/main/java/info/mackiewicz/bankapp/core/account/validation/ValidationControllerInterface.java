package info.mackiewicz.bankapp.core.account.validation;

import info.mackiewicz.bankapp.core.account.validation.dto.ValidationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface defining REST API endpoints for validation operations.
 * <p>
 * This controller interface provides operations for validating IBAN numbers
 * and checking if email addresses exist within the system.
 * <p>
 * Note: Authentication is required. The user must be logged in to access these endpoints.
 */
@SecurityRequirement(name = "cookieAuth")
@Tag(name = "Account ID Validation", description = "Operations for validating data such as IBAN and email addresses")
@RequestMapping("/api/account/validate")
public interface ValidationControllerInterface {

    @Operation(
            summary = "Validate IBAN number",
            description = """
                    Validates the format of a provided IBAN number.
                    
                    User must be authenticated to access this endpoint."""
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Validation completed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - empty IBAN",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during validation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResponse.class))
            )
    })
    @GetMapping("/iban")
    ResponseEntity<ValidationResponse> validateIban(
            @Parameter(
                    description = "IBAN number to validate",
                    required = true
            ) @RequestParam String iban);


    @Operation(
            summary = "Validate email address",
            description = """
                    Validates the format of a provided email address and checks if an account exists for this email.
                    
                    User must be authenticated to access this endpoint."""
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Validation completed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - empty email or incorrect format",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error during validation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResponse.class))
            )
    })
    @GetMapping("/email")
    ResponseEntity<ValidationResponse> validateEmail(
            @Parameter(
                    description = "Email address to validate",
                    required = true
            ) @RequestParam String email);
}

