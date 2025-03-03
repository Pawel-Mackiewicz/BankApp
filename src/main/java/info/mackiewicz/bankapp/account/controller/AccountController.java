package info.mackiewicz.bankapp.account.controller;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.dto.CreateAccountRequest;
import info.mackiewicz.bankapp.account.service.AccountService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    // GET /api/accounts/{id} - Retrieve an account by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable int id) {
            Account account = accountService.getAccountById(id);
            return ResponseEntity
                    .ok(account);
    }

    // GET /api/accounts - Retrieve all accounts
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    // GET /api/accounts/owner/{pesel} - Retrieve accounts by owner's PESEL
    @GetMapping("/owner/{pesel}")
    public ResponseEntity<List<Account>> getAccountsByOwnerPesel(@PathVariable String pesel) {
        List<Account> accounts = accountService.getAccountsByOwnersPESEL(pesel);
        return ResponseEntity.ok(accounts);
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
            Account account = accountService.createAccount(request.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }



    // DELETE /api/accounts/{id} - Delete an account by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable int id) {
            accountService.deleteAccountById(id);
            return ResponseEntity.noContent().build();
    }
}
