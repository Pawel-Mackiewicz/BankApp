package info.mackiewicz.bankapp.system.banking.operations.api;

import info.mackiewicz.bankapp.shared.web.dto.BaseApiError;
import info.mackiewicz.bankapp.shared.web.dto.ValidationApiError;
import info.mackiewicz.bankapp.system.banking.operations.api.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.system.banking.operations.api.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.system.banking.shared.dto.TransactionResponse;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Interface defining basic banking operations API endpoints.
 * Provides contract for money transfers, withdrawals and deposits.
 */
@Tag(name = "Banking Operations", description = "API for performing banking operations like transfers, withdrawals and deposits")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/banking")
public interface BankingOperationsControllerInterface {

    /**
     * Transfers funds between accounts
     * 
     * @param request transfer details including source account IBAN, destination account IBAN and amount
     * @param authUser authenticated user details, who has access to the source account
     * @return response with transaction result
     */
    @Operation(
        summary = "Transfer funds using IBAN", 
        description = "Transfers funds from source account to destination account using IBAN identifiers. \n\n" +
                     "Before making transfer, you should **validate recipient's IBAN using GET /api/validate-iban** endpoint."
    )
    @PostMapping("/transfer/iban")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Transfer completed successfully", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = TransactionResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Successful transfer",
                        summary = "Example of a successful transfer response",
                        value = """
                                {
                                  "sourceAccount": {
                                    "fullNameOfOwner": "John Smith",
                                    "iban": "PL12345678901234567890123456"
                                  },
                                
                                  "targetAccount": {
                                    "fullNameOfOwner": "Alice Johnson",
                                    "iban": "PL09876543210987654321098765"
                                  },
                                
                                  "transactionInfo": {
                                    "id": 123,
                                    "amount": 100.50,
                                    "type": "TRANSFER_INTERNAL",
                                    "title": "Services",
                                    "date": "2023-04-01T12:34:56Z",
                                    "status": "DONE"
                                  }
                                }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data, insufficient funds or validation error",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ValidationApiError.class),
                examples = {
                    @ExampleObject(
                        name = "Validation error",
                        summary = "Example of a validation error response",
                        value = """
                                {
                                  "path": "/api/banking/transfer/iban",
                                  "errors": [
                                    {
                                      "field": "recipientIban",
                                      "message": "Invalid IBAN format",
                                      "rejectedValue": "PL9148511340003570000000002"
                                    }
                                  ],
                                  "status": "BAD_REQUEST",
                                  "title": "VALIDATION_ERROR",
                                  "message": "Validation failed. Please check your input and try again.",
                                  "timestamp": "04-04-2025 13:35:07"
                                }"""
                    ),
                    @ExampleObject(
                        name = "Insufficient funds error",
                        summary = "Example of an insufficient funds error response",
                        value = """
                                {
                                  "path": "/api/banking/transfer/iban",
                                  "status": "BAD_REQUEST",
                                  "title": "INSUFFICIENT_FUNDS",
                                  "message": "Insufficient funds for this transaction. Please check your balance and try again.",
                                  "timestamp": "04-04-2025 13:35:07"
                                }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "User does not own the source account", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = BaseApiError.class),
                examples = {
                    @ExampleObject(
                        name = "Account ownership error",
                        summary = "Example of an account ownership error response",
                        value = """
                                {
                                  "path": "/api/banking/transfer/iban",
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
            responseCode = "404", 
            description = "Source or destination account not found", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = BaseApiError.class),
                examples = {
                    @ExampleObject(
                        name = "Account not found error",
                        summary = "Example of an account not found error response",
                        value = """
                                {
                                  "path": "/api/banking/transfer/iban",
                                  "status": "NOT_FOUND",
                                  "title": "ACCOUNT_NOT_FOUND",
                                  "message": "Account not found.",
                                  "timestamp": "04-04-2025 13:35:07"
                                }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during transaction processing", 
            content = @Content
        )
    })
    ResponseEntity<TransactionResponse> ibanTransfer(
            @Parameter(
                description = "Transfer details including source and recipient IBANs", 
                required = true
            )
            @Valid @RequestBody IbanTransferRequest request, 
            
            @Parameter(
                description = "Authenticated user details", 
                hidden = true
            )
            @AuthenticationPrincipal UserDetailsWithId authUser);


    /**
     * Transfers funds to an email address
     * 
     * @param request transfer details including source account, destination email (which will be resolved to IBAN) and amount
     * @param authUser authenticated user details, who has access to the source account
     * @return response with transaction result
     */
    @Operation(
        summary = "Transfer funds using email", 
        description = """
                Transfers funds from source account to the account associated with the provided email address.\s
                
                Before making transfer, you should **validate recipient's email using GET /api/validate-email** endpoint."""
    )
    @PostMapping("/transfer/email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer completed successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransactionResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "Successful email transfer",
                            summary = "Example of a successful transfer using email",
                            value = """
                                    {
                                      "sourceAccount": {
                                        "formattedIban": "PL91 4851 1234 0000 5700 0000 0002",
                                        "ownerFullname": "Marian Ziolkowski"
                                      },
                                      "targetAccount": {
                                        "formattedIban": "PL92 4851 1234 0000 2300 0000 0001",
                                        "ownerFullname": "Pablo Picasso"
                                      },
                                      "transactionInfo": {
                                        "id": 81,
                                        "type": "TRANSFER_INTERNAL",
                                        "date": "2025-04-04T16:15:34.6386211",
                                        "title": "string",
                                        "amount": 0.01,
                                        "status": "NEW"
                                      }
                                    }"""
                        )
                    })),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input, insufficient funds or validation error", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = BaseApiError.class),
                examples = {
                    @ExampleObject(
                        name = "Insufficient funds error",
                        summary = "Example of an insufficient funds error response",
                        value = """
                                {
                                  "path": "/api/banking/transfer/email",
                                  "status": "BAD_REQUEST",
                                  "title": "INSUFFICIENT_FUNDS",
                                  "message": "Insufficient funds for this transaction. Please check your balance and try again.",
                                  "timestamp": "04-04-2025 13:35:07"
                                }"""
                    ),
                    @ExampleObject(
                        name = "Validation error",
                        summary = "Example of a validation error response",
                        value = """
                                {
                                  "path": "/api/banking/transfer/iban",
                                  "errors": [
                                    {
                                      "field": "recipientIban",
                                      "message": "Invalid IBAN format",
                                      "rejectedValue": "PL9148511340003570000000002"
                                    }
                                  ],
                                  "status": "BAD_REQUEST",
                                  "title": "VALIDATION_ERROR",
                                  "message": "Validation failed. Please check your input and try again.",
                                  "timestamp": "04-04-2025 13:35:07"
                                }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "User does not own the source account", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = BaseApiError.class),
                examples = {
                    @ExampleObject(
                        name = "Account ownership error",
                        summary = "Example of an account ownership error response",
                        value = """
                                {
                                  "path": "/api/banking/transfer/email",
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
            responseCode = "404", 
            description = "Source or destination account not found", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = BaseApiError.class),
                examples = {
                    @ExampleObject(
                        name = "Account not found error",
                        summary = "Example of an account not found error response",
                        value = """
                                {
                                  "path": "/api/banking/transfer/email",
                                  "status": "NOT_FOUND",
                                  "title": "ACCOUNT_NOT_FOUND",
                                  "message": "Account not found.",
                                  "timestamp": "04-04-2025 13:35:07"
                                }"""
                    ),
                    @ExampleObject(
                        name = "Destination account not found error",
                        summary = "Example of a destination account not found with a given email error response",
                        value = """
                                {
                                  "path": "/api/banking/transfer/email",
                                  "status": "NOT_FOUND",
                                  "title": "ACCOUNT_OWNER_NOT_FOUND",
                                  "message": "Account not found.",
                                  "timestamp": "04-04-2025 13:35:07"
                                }"""
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during transaction processing", 
            content = @Content
        )
    })
    ResponseEntity<TransactionResponse> emailTransfer(
            @Parameter(description = "Transfer details including source IBAN and recipient email", required = true,
                    examples = {
                        @ExampleObject(
                            name = "Email transfer",
                            summary = "Example of a transfer using recipient's email address",
                            value = """
                                    {
                                      "sourceIban": "PL12345678901234567890123456",
                                      "amount": 100.50,
                                      "title": "Transfer for Jan",
                                      "destinationEmail": "jan.kowalski@example.com"
                                    }"""
                        )
                    })
            @Valid @RequestBody EmailTransferRequest request, 
            
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetailsWithId authUser);
}