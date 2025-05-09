package info.mackiewicz.bankapp.core.account.service;

import info.mackiewicz.bankapp.core.account.exception.AccountDeletionException;
import info.mackiewicz.bankapp.core.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.repository.AccountRepository;
import info.mackiewicz.bankapp.core.account.service.interfaces.AccountServiceInterface;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.model.vo.Pesel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService implements AccountServiceInterface {

    private final AccountRepository accountRepository;
    private final AccountOperationsService accountOperationsService;
    private final AccountQueryService accountQueryService;
    private final AccountCreationService accountCreationService;

    /**
     * Creates a new account for the specified user.
     * <p>
     * This method attempts to create an account and will retry if it fails,
     * up to a maximum number of retries with increasing delays between attempts.
     * </p>
     *
     * @param userId The ID of the user who will own the account
     *
     * @return The newly created account
     * @throws RuntimeException if account creation fails after all retry attempts
     */
    @Override
    @Transactional
    public Account createAccount(@NotNull Integer userId) {
        log.debug("Delegating account creation to AccountCreationService for user ID: {}", userId);
        return accountCreationService.createAccount(userId);
    }

    /**
     * Retrieves an account by its ID.
     *
     * @param id The ID of the account to retrieve
     *
     * @return The account with the specified ID
     * @throws AccountNotFoundByIdException if no account is found with the given ID
     */
    @Override
    public Account getAccountById(int id) {
        return accountQueryService.getAccountById(id);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountQueryService.getAllAccounts();
    }

    @Deprecated(since = "0.4.8")
    @Override
    public List<Account> getAccountsByOwnersPesel(String pesel) {
        return getAccountsByOwnersPesel(new Pesel(pesel));
    }

    public List<Account> getAccountsByOwnersPesel(Pesel pesel) {
        return accountQueryService.getAccountsByOwnersPesel(pesel);
    }

    @Override
    public List<Account> getAccountsByOwnersUsername(String username) {
        return accountQueryService.getAccountsByOwnersUsername(username);
    }

    @Override
    public List<Account> getAccountsByOwnersId(Integer id) {
        return accountQueryService.getAccountsByOwnersId(id);
    }

    @Deprecated(since = "0.4.6")
    @Override
    public Account getAccountByOwnersEmail(String recipientEmail) {
        return accountQueryService.getAccountByOwnersEmail(new EmailAddress(recipientEmail));
    }

    @Override
    public Account getAccountByOwnersEmail(EmailAddress recipientEmail) {
        return accountQueryService.getAccountByOwnersEmail(recipientEmail);
    }

    @Deprecated(since = "0.4.6")
    @Override
    public Account getAccountByIban(String iban) {
        return accountQueryService.getAccountByIban(Iban.valueOf(iban));
    }

    @Override
    public Account getAccountByIban(Iban iban) {
        return accountQueryService.getAccountByIban(iban);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return accountQueryService.existsByEmail(email);
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Account deposit(Account account,
                           @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        return accountOperationsService.deposit(account, amount);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Account withdraw(Account account,
                            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        return accountOperationsService.withdraw(account, amount);
    }

    @Override
    @Transactional
    public void deleteAccountById(int id) {
        //TODO: SOFT DELETE
        log.debug("Deleting account with ID: {}", id);
        Account account = getAccountById(id);
        if (account.getBalance() == null || account.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            accountRepository.delete(account);
        } else {
            throw new AccountDeletionException("Account with ID " + id + " has non-zero balance and cannot be deleted.");
        }
    }
}