package info.mackiewicz.bankapp.transaction.controller;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.dto.CreateTransactionRequest;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing transaction operations.
 * Provides endpoints for transaction creation, processing, and retrieval.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
public class TransactionController implements TransactionControllerInterface {

    private final TransactionService transactionService;
    private final AccountService accountService;

    @Override
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        Account sourceAccount = request.getSourceAccountId() != null ? 
            accountService.getAccountById(request.getSourceAccountId()) : null;
        Account destinationAccount = request.getDestinationAccountId() != null ? 
            accountService.getAccountById(request.getDestinationAccountId()) : null;

        Transaction transaction = Transaction.buildTransfer()
            .from(sourceAccount)
            .to(destinationAccount)
            .withAmount(request.getAmount())
            .withTitle(request.getTitle())
            .build();

        Transaction savedTransaction = transactionService.registerTransaction(transaction);
        return ResponseEntity.status(201).body(savedTransaction);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionById(@PathVariable int id) {
        transactionService.deleteTransactionById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable int id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @Override
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable int accountId) {
        // Weryfikacja czy konto istnieje
        accountService.getAccountById(accountId);
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }
}
