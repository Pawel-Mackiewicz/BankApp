package info.mackiewicz.bankapp.transaction.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.dto.CreateTransactionRequest;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
            .withTransactionType(request.getType())
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

    @Override
    @PostMapping("/{id}/process")
    public ResponseEntity<?> processTransactionById(@PathVariable int id) {
        transactionService.processTransactionById(id);
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @Override
    @PostMapping("/process-all")
    public ResponseEntity<String> processAllNewTransactions() {
        transactionService.processAllNewTransactions();
        return ResponseEntity.ok().build();
    }
}
