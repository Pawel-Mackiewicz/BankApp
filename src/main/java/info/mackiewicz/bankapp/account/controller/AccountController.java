package info.mackiewicz.bankapp.account.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import lombok.RequiredArgsConstructor;

/**
 * REST controller responsible for managing bank accounts.
 * This controller provides endpoints for creating, retrieving, and deleting bank accounts.
 * All operations require appropriate authentication and authorization.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    /**
     * Retrieves a bank account by its ID.
     *
     * @param id The unique identifier of the account
     * @return ResponseEntity containing the account if found
     * @throws info.mackiewicz.bankapp.account.exception.AccountNotFoundByIdException if account not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable int id) {
        Account account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    /**
     * Retrieves all bank accounts in the system.
     *
     * @return ResponseEntity containing a list of all accounts
     */
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Retrieves all bank accounts owned by a person with given PESEL number.
     *
     * @param pesel The PESEL number of the account owner
     * @return ResponseEntity containing a list of accounts owned by the person
     * @throws info.mackiewicz.bankapp.account.exception.OwnerAccountsNotFoundException if no accounts found
     */
    //GET /api/accounts/owner/{pesel}
    @GetMapping("/owner/{pesel}")
    public ResponseEntity<List<Account>> getAccountsByOwnerPesel(@PathVariable String pesel) {
        List<Account> accounts = accountService.getAccountsByOwnersPesel(pesel);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Creates a new bank account for a specified user.
     * Endpoint: POST /api/accounts/createFor/{userId}
     *
     * @param userId user ID for whom to create the account
     * @return ResponseEntity containing the newly created account
     */
    //POST /api/accounts/createFor/{userId}
    @PostMapping("/createFor/{userId}")
    public ResponseEntity<Account> createAccount(@PathVariable int userId) {
        Account account = accountService.createAccount(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    /**
     * Deletes a bank account with the specified ID.
     *
     * @param id The unique identifier of the account to delete
     * @return ResponseEntity with no content on successful deletion
     * @throws info.mackiewicz.bankapp.account.exception.AccountNotFoundByIdException if account not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable int id) {
        accountService.deleteAccountById(id);
        return ResponseEntity.noContent().build();
    }
}
