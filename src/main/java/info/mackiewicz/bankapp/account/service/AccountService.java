package info.mackiewicz.bankapp.account.service;

import java.math.BigDecimal;
import java.util.List;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.account.service.interfaces.AccountServiceInterface;
import info.mackiewicz.bankapp.user.model.vo.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
     * @return The account with the specified ID
     * @throws info.mackiewicz.bankapp.account.exception.AccountNotFoundByIdException if no account is found with the given ID
     */
    @Override
    public Account getAccountById(int id) {
        return accountQueryService.getAccountById(id);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountQueryService.getAllAccounts();
    }

    @Override
    public List<Account> getAccountsByOwnersPesel(String pesel) {
        return accountQueryService.getAccountsByOwnersPesel(pesel);
    }

    @Override
    public List<Account> getAccountsByOwnersUsername(String username) {
        return accountQueryService.getAccountsByOwnersUsername(username);
    }

    /**
     * Retrieves all accounts associated with the specified owner's identifier.
     *
     * @param id the unique identifier of the account owner
     * @return a list of accounts linked to the owner, or an empty list if no accounts are found
     */
    @Override
    public List<Account> getAccountsByOwnersId(Integer id) {
        return accountQueryService.getAccountsByOwnersId(id);
    }

    /**
     * Retrieves the account associated with the specified owner's email.
     *
     * <p><strong>Deprecated:</strong> This legacy method accepts the email as a {@code String}. Use
     * {@link #getAccountByOwnersEmail(Email)} with an {@code Email} object for improved type safety.
     *
     * @param recipientEmail the email address of the account owner
     * @return the account associated with the specified email, or {@code null} if no such account exists
     * @deprecated Use {@link #getAccountByOwnersEmail(Email)} instead.
     */
    @Deprecated
    @Override
    public Account getAccountByOwnersEmail(String recipientEmail) {
        return accountQueryService.findAccountByOwnersEmail(recipientEmail);
    }

    /**
     * Retrieves the account associated with the specified owner's email.
     *
     * @param recipientEmail the validated Email object representing the account owner's email address
     * @return the account corresponding to the provided email address
     */
    @Override
    public Account getAccountByOwnersEmail(Email recipientEmail) {
        return accountQueryService.getAccountByOwnersEmail(recipientEmail);
    }

    /**
     * Retrieves the account associated with the specified IBAN.
     *
     * @param iban the account's IBAN as a String
     * @return the account corresponding to the given IBAN
     * @deprecated Use {@link #getAccountByIban(Iban)} for improved type-safety with validated IBAN objects.
     */
    @Deprecated
    @Override
    public Account getAccountByIban(String iban) {
        return accountQueryService.getAccountByIban(iban);
    }

    /**
     * Retrieves an account corresponding to the provided IBAN.
     *
     * @param iban the IBAN identifying the account to retrieve
     * @return the account associated with the specified IBAN
     */
    @Override
    public Account getAccountByIban(Iban iban) {
        return accountQueryService.getAccountByIban(iban);
    }

    /**
     * Checks whether an account exists for the specified email address.
     *
     * Delegates the existence check to the account query service.
     *
     * @param email the email address to search for an associated account
     * @return true if an account is found for the given email, false otherwise
     */
    @Override
    public boolean existsByEmail(String email) {
        return accountQueryService.existsByEmail(email);
    }

    @Override
    @Transactional
    public void deleteAccountById(int id) {
        log.debug("Deleting account with ID: {}", id);
        Account account = getAccountById(id);
        accountRepository.delete(account);
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
}