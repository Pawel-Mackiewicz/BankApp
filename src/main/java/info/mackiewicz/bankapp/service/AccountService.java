package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.AccountRepository;
import info.mackiewicz.bankapp.utils.IbanGenerator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    @Transactional
    public Account createAccount(Integer userId) {
        User user = userService.getUserById(userId);
        Account account = new Account(user);
        account = setupIban(account);

        return accountRepository.save(account);
    }

    public Account setupIban(Account account) {
        String iban = IbanGenerator.generateIban(account.getOwner().getId(), account.getUserAccountNumber());
        account.setIban(iban);
        return account;
    }

    public Account getAccountById(int id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundByIdException("Account with ID " + id + " not found."));
    }

    public List<Account> getAccountsByOwnersPESEL(String pesel) {
        return accountRepository.findAccountsByOwner_PESEL(pesel)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        "User with PESEL " + pesel + " does not have any account."));
    }

    public List<Account> getAccountsByOwnersUsername(String username) {
        return accountRepository.findAccountsByOwner_username(username)
                .orElseThrow(
                        () -> new OwnerAccountsNotFoundException("User: " + username + " does not have any account."));
    }

    public List<Account> getAccountsByOwnersId(Integer id) {
        return accountRepository.findAccountsByOwner_id(id)
                .orElseThrow(
                        () -> new OwnerAccountsNotFoundException("User with ID " + id + " does not have any account."));
    }

    public Optional<Account> findByIban(String iban) {
        return accountRepository.findByIban(iban);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void deleteAccountById(int id) {
        Account account = getAccountById(id);
        accountRepository.delete(account);
    }

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
