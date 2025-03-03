package info.mackiewicz.bankapp.account.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.AccountFactory;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.shared.exception.InvalidOperationException;
import info.mackiewicz.bankapp.shared.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class AccountService implements AccountServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final AccountFactory accountFactory;

    @Override
    @Transactional
    public Account createAccount(@NotNull Integer userId) {
        logger.debug("Creating account for user ID: {}", userId);
        User user = userService.getUserById(userId);
        Account account = accountFactory.createAccount(user);
        return accountRepository.save(account);
    }

    @Override
    public Account getAccountById(int id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundByIdException("Account with ID " + id + " not found."));
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> findAccountByIban(String iban) {
        logger.debug("Finding account by IBAN: {}", iban);
        return accountRepository.findByIban(iban);
    }

    @Override
    public List<Account> getAccountsByOwnerCriteria(String value, Function<String, Optional<List<Account>>> finder,
            String criteriaName) {
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

    @Override
    public Optional<Account> findAccountByOwnersEmail(@Email(message = "Invalid email format") String recipientEmail) {
        logger.debug("Finding account by owner's email: {}", recipientEmail);
        return accountRepository.findFirstByOwner_email(recipientEmail);
    }

    @Override
    @Transactional
    public void deleteAccountById(int id) {
        logger.debug("Deleting account with ID: {}", id);
        Account account = getAccountById(id);
        accountRepository.delete(account);
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
    public Account deposit(int accountId,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Deposit amount must be greater than zero");
        }

        Account account = getAccountById(accountId);
        account.deposit(amount);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account withdraw(int accountId,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Withdrawal amount must be greater than zero");
        }

        Account account = getAccountById(accountId);
        account.withdraw(amount);
        return accountRepository.save(account);
    }
}