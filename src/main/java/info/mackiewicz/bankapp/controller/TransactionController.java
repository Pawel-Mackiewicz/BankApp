package info.mackiewicz.bankapp.controller;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionBuilder;
import info.mackiewicz.bankapp.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//BY CHATGPT
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;

    // Constructor injection
    public TransactionController(TransactionService transactionService,
                                 TransactionBuilder transactionBuilder) {
        this.transactionService = transactionService;
        this.transactionBuilder = transactionBuilder;
    }

    // Create a new transaction.
    // POST /api/transactions
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody CreateTransactionRequest request) {
        Transaction transaction = transactionBuilder
                .withSourceAccount(request.getSourceAccountId()) // could be null for DEPOSIT
                .withDestinationAccount(request.getDestinationAccountId())  // could be null for WITHDRAW, FEE
                .withAmount(request.getAmount())
                .withType(request.getType())
                .withTransactionTitle(request.getTitle())
                .build();

        // Save transaction via service layer
        Transaction savedTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    //Delete transaction by its ID.
    // DELETE /api/transactions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionById(@PathVariable int id) {
            transactionService.deleteTransactionById(id);
            return ResponseEntity.ok().build();
    }

    // Get a transaction by its ID.
    // GET /api/transactions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable int id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    // Get all transactions.
    // GET /api/transactions
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    // Get transactions by account ID.
    // GET /api/transactions/account/{accountId}
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable int accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    // Process a specific transaction by its ID.
    // POST /api/transactions/{id}/process
    @PostMapping("/{id}/process")
    public ResponseEntity<?> processTransactionById(@PathVariable int id) {
            transactionService.processTransactionById(id);
            return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    // Process all transactions.
    // POST /api/transactions/process-all
    @PostMapping("/process-all")
    public ResponseEntity<String> processAllNewTransactions() {
            transactionService.processAllNewTransactions();
            return ResponseEntity.ok().build();
    }
}
