package info.mackiewicz.bankapp.controller.api;

import info.mackiewicz.bankapp.dto.CreateTransactionRequest;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionBuilder;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody CreateTransactionRequest request) {
        Account sourceAccount = request.getSourceAccountId() != null ? 
            accountService.getAccountById(request.getSourceAccountId()) : null;
        Account destinationAccount = request.getDestinationAccountId() != null ? 
            accountService.getAccountById(request.getDestinationAccountId()) : null;

        Transaction transaction = transactionBuilder
                .withSourceAccount(sourceAccount)
                .withDestinationAccount(destinationAccount)
                .withAmount(request.getAmount())
                .withType(request.getType())
                .withTransactionTitle(request.getTitle())
                .build();

        Transaction savedTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionById(@PathVariable int id) {
        transactionService.deleteTransactionById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable int id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable int accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<?> processTransactionById(@PathVariable int id) {
        transactionService.processTransactionById(id);
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping("/process-all")
    public ResponseEntity<String> processAllNewTransactions() {
        transactionService.processAllNewTransactions();
        return ResponseEntity.ok().build();
    }
}
