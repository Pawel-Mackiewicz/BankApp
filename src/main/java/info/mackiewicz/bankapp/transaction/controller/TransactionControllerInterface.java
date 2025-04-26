package info.mackiewicz.bankapp.transaction.controller;

import info.mackiewicz.bankapp.system.error.handling.dto.BaseApiError;
import info.mackiewicz.bankapp.system.error.handling.dto.ValidationApiError;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.dto.CreateTransactionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionControllerInterface {

    @Operation(summary = "Create a new transaction", description = "Creates a new transaction in the system. The transaction will be registered with the provided details but not processed immediately.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Transaction details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateTransactionRequest.class), examples = {
            @ExampleObject(name = "Standard transaction", value = "{\n" +
                    "  \"sourceAccountId\": 1,\n" +
                    "  \"destinationAccountId\": 2,\n" +
                    "  \"amount\": 100.00,\n" +
                    "  \"title\": \"Payment for services\",\n" +
                    "  \"type\": \"TRANSFER_INTERNAL\"\n" +
                    "}")
    }))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Transaction.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationApiError.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class)))
    })
    ResponseEntity<Transaction> createTransaction(CreateTransactionRequest request);

    @Operation(summary = "Delete transaction by ID", description = "Deletes a transaction by its unique ID. Only unprocessed transactions can be deleted.")
    @Parameter(required = true, description = "Transaction's unique identifier", name = "id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))),
            @ApiResponse(responseCode = "400", description = "Cannot delete processed transaction", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class)))
    })
    ResponseEntity<Void> deleteTransactionById(int id);

    @Operation(summary = "Get transaction by ID", description = "Retrieves a transaction by its unique ID. Returns transaction details if found.")
    @Parameter(required = true, description = "Transaction's unique identifier", name = "id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Transaction.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class)))
    })
    ResponseEntity<Transaction> getTransactionById(int id);

    @Operation(summary = "Get all transactions", description = "Retrieves a list of all transactions in the system. Returns an empty list if no transactions are found.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of transactions retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Transaction.class)))
    })
    ResponseEntity<List<Transaction>> getAllTransactions();

    @Operation(summary = "Get transactions by account ID", description = "Retrieves all transactions related to a specific account (either as source or destination).")
    @Parameter(required = true, description = "Account's unique identifier", name = "accountId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions found for the account", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Transaction.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class)))
    })
    ResponseEntity<List<Transaction>> getTransactionsByAccountId(int accountId);
}