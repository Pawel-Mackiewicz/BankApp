package info.mackiewicz.bankapp.controller;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionBuilder;
import info.mackiewicz.bankapp.service.AccountService;
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
    private final AccountService accountService;

    // Constructor injection
    public TransactionController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    // Create a new transaction.
    // POST /api/transactions
    //TODO: REFACTOR
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody CreateTransactionRequest request) {

        Account fromAccount;
        Account toAccount;

        if (request.getFromAccountId() == 0) fromAccount = null;
        else fromAccount = accountService.getAccountById(request.getFromAccountId());
        if (request.getToAccountId() == 0) toAccount = null;
        else toAccount = accountService.getAccountById(request.getToAccountId());

        Transaction transaction = new TransactionBuilder()
                .withFromAccount(fromAccount) // could be null for DEPOSIT
                .withToAccount(toAccount)  // could be null for WITHDRAW
                .withAmount(request.getAmount())
                .withType(request.getType())
                .build();

        // Save transaction via service layer
        Transaction savedTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
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
    public ResponseEntity<Void> processTransactionById(@PathVariable int id) {
        transactionService.processTransactionById(id);
        return ResponseEntity.ok().build();
    }

    // Process all transactions.
    // POST /api/transactions/process-all
    @PostMapping("/process-all")
    public ResponseEntity<Void> processAllTransactions(@RequestBody List<Integer> transactionIds) {
        // Retrieve transactions by their IDs.
        List<Transaction> transactions = transactionIds.stream()
                .map(transactionService::getTransactionById)
                .toList();
        transactionService.processAllTransactions(transactions);
        return ResponseEntity.ok().build();
    }
}
