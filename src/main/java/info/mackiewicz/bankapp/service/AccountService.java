package info.mackiewicz.bankapp.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import info.mackiewicz.bankapp.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.exception.InvalidOperationException;
import info.mackiewicz.bankapp.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.AccountRepository;
import info.mackiewicz.bankapp.utils.IbanGenerator;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService implements AccountServiceInterface {

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Account createAccount(@NotNull Integer userId) {
        log.debug("Creating account for user ID: {}", userId);

        User user = userService.getUserById(userId);
        log.info("createAccount: userService.getUserById returned: {}", user);
        Account account = new Account(user);
        log.info("createAccount: Account created: {}", account);
        account = setupIban(account);
        log.info("createAccount: setupIban returned: {}", account);

        log.info("createAccount: Calling accountRepository.save with account: {}", account);
        Account savedAccount = accountRepository.save(account);
        log.info("createAccount: accountRepository.save returned: {}", savedAccount);
        log.info("createAccount: Ending");
        return savedAccount;
    }

    private Account setupIban(Account account) {
        String iban = IbanGenerator.generateIban(account.getOwner().getId(), account.getUserAccountNumber());
        account.setIban(iban);
        return account;
    }

    @Override
    public Account getAccountById(int id) {
        log.info("getAccountById: Starting with id: {}", id);
        Optional<Account> account = accountRepository.findById(id);
        log.info("getAccountById: accountRepository.findById returned: {}", account);
        Account result = account.orElseThrow(() -> new AccountNotFoundByIdException("Account with ID " + id + " not found."));
        log.info("getAccountById: Ending with result: {}", result);
        return result;
    }

    @Override
    public List<Account> getAccountsByOwnerCriteria(String value, Function<String, Optional<List<Account>>> finder, String criteriaName) {
        return finder.apply(value)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with %s %s does not have any account.", criteriaName, value)));
    }

    @Override
    public List<Account> getAccountsByOwnersPESEL(String pesel) {
        return getAccountsByOwnerCriteria(pesel, accountRepository::findAccountsByOwner_PESEL, "PESEL");
    }

    @Override
    public List<Account> getAccountsByOwnersUsername(String username) {
        return getAccountsByOwnerCriteria(username, accountRepository::findAccountsByOwner_username, "username");
    }

    @Override
    public List<Account> getAccountsByOwnersId(Integer id) {
        return getAccountsByOwnerCriteria(id.toString(), 
            value -> accountRepository.findAccountsByOwner_id(Integer.parseInt(value)), 
            "ID");
    }
  
      public Optional<Account> findAccountByOwnersEmail(@Email(message = "Invalid email format") String recipientEmail) {
        log.info("findAccountByOwnersEmail: Starting with email: {}", recipientEmail);
        Optional<Account> account = accountRepository.findFirstByOwner_email(recipientEmail);
        log.info("findAccountByOwnersEmail: findFirstByOwner_email returned: {}", account);
        return account;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findAccountByIban(String iban) {
        log.info("findAccountByIban: Starting with IBAN: {}", iban);
        Optional<Account> account = accountRepository.findByIban(iban);
        log.info("findAccountByIban: findByIban returned: {}", account);
        return account;
    }

    @Override
    public Optional<Account> findAccountByOwnersEmail(@Email(message = "Invalid email format") String recipientEmail) {
        log.debug("Finding account by owner's email: {}", recipientEmail);
        return accountRepository.findFirstByOwner_email(recipientEmail);
    }

    @Override
    @Transactional
    public void deleteAccountById(int id) {
        log.debug("Deleting account with ID: {}", id);
        log.info("deleteAccountById: Starting with id: {}", id);
        Account account = getAccountById(id);
        log.info("deleteAccountById: getAccountById returned: {}", account);
        accountRepository.delete(account);
        log.info("deleteAccountById: accountRepository.delete called");
        log.info("deleteAccountById: Ending");
    }

    @Override
    @Transactional
    public Account changeAccountOwner(int accountId, int newOwnerId) {
        Account account = getAccountById(accountId);
        User newOwner = userService.getUserById(newOwnerId);
        account.setOwner(newOwner);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account deposit(int accountId, @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Deposit amount must be greater than zero");
        }

    @Override
    @Transactional
    public Account withdraw(int accountId, @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Withdrawal amount must be greater than zero");
        }
}
