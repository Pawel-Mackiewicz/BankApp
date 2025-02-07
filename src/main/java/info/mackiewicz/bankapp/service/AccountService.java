package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    // Constructor injection of the repository
    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    // CREATE: Creates a new account with the given owner.
    @Transactional
    public Account createAccount(Integer userId) {
        User user = validateAndReattachOwner(userId);
        Account account = new Account(user);
        return accountRepository.save(account);
    }

    private User validateAndReattachOwner(int userId) {
        return userService.getUserById(userId);
    }

    // READ: Retrieves an account by its ID. Throws an exception if not found.
    public Account getAccountById(int id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account with ID " + id + " does not exist."));
    }

    // READ: Retrieves accounts by the owner's PESEL (unique identifier).
    public List<Account> getAccountsByOwnersPESEL(String pesel) {
        return accountRepository.findAccountsByOwner_PESEL(pesel)
                .orElseThrow(() -> new RuntimeException("User with PESEL " + pesel + " does not have any account."));
    }

    // READ: Retrieves all accounts.
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // DELETE: Deletes an account by its ID.
    // Throws an exception if the account does not exist.
    public void deleteAccountById(int id) {
        Account account = getAccountById(id);
        accountRepository.delete(account);
    }

    // UPDATE: Changes the owner of an account.
    public Account changeAccountOwner(int id, int newId) {
        Account account = getAccountById(id);
        account.setOwner(userService.getUserById(newId));
        return accountRepository.save(account);
    }

    // FINANCIAL OPERATIONS

    // DEPOSIT: Deposits a specified amount into an account.
    // The operation is wrapped in a transactional context.
    @Transactional
    public Account deposit(int accountId, BigDecimal amount) {
        Account account = getAccountById(accountId);
        account.deposit(amount);
        return accountRepository.save(account);
    }

    // WITHDRAW: Withdraws a specified amount fromAccount an account.
    // The operation is wrapped in a transactional context.
    @Transactional
    public Account withdraw(int accountId, BigDecimal amount) {
        Account account = getAccountById(accountId);
        account.withdraw(amount);
        return accountRepository.save(account);
    }
}
