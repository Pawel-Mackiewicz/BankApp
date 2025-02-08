package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.exception.OwnerAccountsNotFoundException;
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

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    // CREATE: Tworzy nowe konto dla danego użytkownika.
    @Transactional
    public Account createAccount(Integer userId) {
        User user = validateAndReattachOwner(userId);
        Account account = new Account(user);
        return accountRepository.save(account);
    }

    private User validateAndReattachOwner(int userId) {
        return userService.getUserById(userId);
    }

    // READ: Pobiera konto po ID. Jeśli nie znaleziono, rzuca AccountNotFoundByIdException.
    public Account getAccountById(int id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundByIdException("Account with ID " + id + " does not exist."));
    }

    // READ: Pobiera konta na podstawie numeru PESEL właściciela.
    public List<Account> getAccountsByOwnersPESEL(String pesel) {
        return accountRepository.findAccountsByOwner_PESEL(pesel)
                .orElseThrow(() -> new OwnerAccountsNotFoundException("User with PESEL " + pesel + " does not have any account."));
    }

    // READ: Pobiera wszystkie konta.
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // DELETE: Usuwa konto po ID. Jeśli konto nie istnieje – rzuca wyjątek.
    public void deleteAccountById(int id) {
        Account account = getAccountById(id);
        accountRepository.delete(account);
    }

    // UPDATE: Zmienia właściciela konta.
    public Account changeAccountOwner(int id, int newId) {
        Account account = getAccountById(id);
        account.setOwner(userService.getUserById(newId));
        return accountRepository.save(account);
    }

    // FINANCIAL OPERATIONS

    @Transactional
    public Account deposit(int accountId, BigDecimal amount) {
        Account account = getAccountById(accountId);
        account.deposit(amount);
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(int accountId, BigDecimal amount) {
        Account account = getAccountById(accountId);
        account.withdraw(amount);
        return accountRepository.save(account);
    }
}
