package info.mackiewicz.bankapp.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import info.mackiewicz.bankapp.transaction.model.dto.BankingOperationRequest;
import info.mackiewicz.bankapp.transaction.model.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.TransferResponse;
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

/**
 * Interface defining basic banking operations API endpoints.
 * Provides contract for money transfers, withdrawals and deposits.
 */
@Tag(name = "Banking Operations", description = "API for performing banking operations like transfers, withdrawals and deposits")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/banking")
public interface BankingOperationsControllerInterface {

    /**
     * Transfers funds between accounts
     * 
     * @param request transfer details including source account IBAN, destination account IBAN and amount
     * @param authUser authenticated user details, who has access to the source account
     * @return response with transaction result
     */
    @Operation(summary = "Transfer funds using IBAN", description = "Transfers funds from source account to destination account using IBAN identifiers")
    @PostMapping("/transfer/iban")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer completed successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransferResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "Successful transfer",
                            summary = "Example of a successful transfer response",
                            value = "{\n" +
                                    "  \"sourceAccount\": {\n" +
                                    "    \"id\": 1,\n" +
                                    "    \"iban\": \"PL12345678901234567890123456\",\n" +
                                    "    \"balance\": 900.50\n" +
                                    "  },\n" +
                                    "  \"targetAccount\": {\n" +
                                    "    \"id\": 2,\n" +
                                    "    \"iban\": \"PL09876543210987654321098765\",\n" +
                                    "    \"balance\": 1100.50\n" +
                                    "  },\n" +
                                    "  \"transactionInfo\": {\n" +
                                    "    \"id\": 123,\n" +
                                    "    \"amount\": 100.50,\n" +
                                    "    \"type\": \"TRANSFER_INTERNAL\",\n" +
                                    "    \"title\": \"Services\",\n" +
                                    "    \"date\": \"2023-04-01T12:34:56Z\",\n" +
                                    "    \"status\": \"DONE\"\n" +
                                    "  }\n" +
                                    "}"
                        )
                    })),
        @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient funds", 
                    content = @Content),
        @ApiResponse(responseCode = "404", description = "Source or destination account not found", 
                    content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not own the source account", 
                    content = @Content),
        @ApiResponse(responseCode = "422", description = "Transaction validation failed", 
                    content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error during transaction processing", 
                    content = @Content)
    })
    ResponseEntity<TransferResponse> ibanTransfer(
            @Parameter(description = "Transfer details including source and recipient IBANs", required = true,
                    examples = {
                        @ExampleObject(
                            name = "Standard IBAN transfer",
                            summary = "Example of a transfer between two accounts using IBAN numbers",
                            value = "{\n" +
                                    "  \"sourceIban\": \"PL12345678901234567890123456\",\n" +
                                    "  \"amount\": 100.50,\n" +
                                    "  \"title\": \"Services\",\n" +
                                    "  \"recipientIban\": \"PL09876543210987654321098765\"\n" +
                                    "}"
                        )
                    })
            @Valid @RequestBody IbanTransferRequest request, 
            
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetailsWithId authUser);

    /**
     * Transfers funds to an email address
     * 
     * @param request transfer details including source account, destination email (which will be resolved to IBAN) and amount
     * @param authUser authenticated user details, who has access to the source account
     * @return response with transaction result
     */
    @Operation(summary = "Transfer funds using email", description = "Transfers funds from source account to the account associated with the provided email address")
    @PostMapping("/transfer/email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transfer completed successfully", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = TransferResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "Successful email transfer",
                            summary = "Example of a successful transfer using email",
                            value = "{\n" +
                                    "  \"sourceAccount\": {\n" +
                                    "    \"id\": 1,\n" +
                                    "    \"iban\": \"PL12345678901234567890123456\",\n" +
                                    "    \"balance\": 900.50\n" +
                                    "  },\n" +
                                    "  \"targetAccount\": {\n" +
                                    "    \"id\": 3,\n" +
                                    "    \"iban\": \"PL45678901234567890123456789\",\n" +
                                    "    \"balance\": 1100.50\n" +
                                    "  },\n" +
                                    "  \"transactionInfo\": {\n" +
                                    "    \"id\": 124,\n" +
                                    "    \"amount\": 100.50,\n" +
                                    "    \"type\": \"TRANSFER_INTERNAL\",\n" +
                                    "    \"title\": \"Services\",\n" +
                                    "    \"date\": \"2023-04-01T12:34:56Z\",\n" +
                                    "    \"status\": \"DONE\"\n" +
                                    "  }\n" +
                                    "}"
                        )
                    })),
        @ApiResponse(responseCode = "400", description = "Invalid input data", 
                    content = @Content),
        @ApiResponse(responseCode = "404", description = "Account not found for the given email", 
                    content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not own the source account", 
                    content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error during transaction processing", 
                    content = @Content)
    })
    ResponseEntity<TransferResponse> emailTransfer(
            @Parameter(description = "Transfer details including source IBAN and recipient email", required = true,
                    examples = {
                        @ExampleObject(
                            name = "Email transfer",
                            summary = "Example of a transfer using recipient's email address",
                            value = "{\n" +
                                    "  \"sourceIban\": \"PL12345678901234567890123456\",\n" +
                                    "  \"amount\": 100.50,\n" +
                                    "  \"title\": \"Transfer for Jan\",\n" +
                                    "  \"destinationEmail\": \"jan.kowalski@example.com\"\n" +
                                    "}"
                        )
                    })
            @Valid @RequestBody EmailTransferRequest request, 
            
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetailsWithId authUser);
    
    /**
     * Withdraws funds from an account
     * Stub method for future implementation.
     * @param request withdrawal details including account and amount
     * @param authUser authenticated user details, who has access to the account
     * @return response with transaction result
     */
    @Operation(summary = "Withdraw funds", description = "STUB. NOT IMPLEMENTED." + " Withdraws funds from the specified account")
    @PostMapping("/api/banking/withdraw")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Withdrawal completed successfully", 
                    content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient funds", 
                    content = @Content),
        @ApiResponse(responseCode = "404", description = "Account not found", 
                    content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not own the account", 
                    content = @Content),
        @ApiResponse(responseCode = "501", description = "Operation not implemented yet", 
                    content = @Content)
    })
    ResponseEntity<?> withdraw(
            @Parameter(description = "Withdrawal details including source IBAN and amount", required = true,
                    examples = {
                        @ExampleObject(
                            name = "Standard withdrawal",
                            summary = "Example of a withdrawal operation",
                            value = "{\n" +
                                    "  \"sourceIban\": \"PL12345678901234567890123456\",\n" +
                                    "  \"amount\": 200.00,\n" +
                                    "  \"title\": \"Withdraw\"\n" +
                                    "}"
                        )
                    })
            @Valid @RequestBody BankingOperationRequest request, 
            
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetailsWithId authUser);
    
    /**
     * Deposits funds to an account
     * Stub method for future implementation.
     * @param request deposit details including account and amount
     * @param authUser authenticated user details, who has access to the account
     * @return response with transaction result
     */
    @Operation(summary = "Deposit funds", description = "STUB. NOT IMPLEMENTED" + " Deposits funds to the specified account")
    @PostMapping("/api/banking/deposit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Deposit completed successfully", 
                    content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input data", 
                    content = @Content),
        @ApiResponse(responseCode = "404", description = "Account not found", 
                    content = @Content),
        @ApiResponse(responseCode = "403", description = "User does not own the account", 
                    content = @Content),
        @ApiResponse(responseCode = "501", description = "Operation not implemented yet", 
                    content = @Content)
    })
    ResponseEntity<?> deposit(
            @Parameter(description = "Deposit details including destination IBAN and amount", required = true,
                    examples = {
                        @ExampleObject(
                            name = "Standard deposit",
                            summary = "Example of a deposit operation",
                            value = "{\n" +
                                    "  \"sourceIban\": \"PL12345678901234567890123456\",\n" +
                                    "  \"amount\": 500.00,\n" +
                                    "  \"title\": \"Deposit\"\n" +
                                    "}"
                        )
                    })
            @Valid @RequestBody BankingOperationRequest request, 
            
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetailsWithId authUser);
}