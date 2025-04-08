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
     * Retrieves accounts associated with a specific owner's ID.
     *
     * @param id the unique identifier of the account owner
     * @return a list of accounts belonging to the specified owner, or an empty list if none are found
     */
    @Override
    public List<Account> getAccountsByOwnersId(Integer id) {
        return accountQueryService.getAccountsByOwnersId(id);
    }

    /**
     * Retrieves an account by the owner's email address.
     *
     * @param recipientEmail the email address associated with the account owner
     * @return the account corresponding to the specified email
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
     * @param recipientEmail the owner's email encapsulated in an Email value object
     * @return the account corresponding to the provided email
     */
    @Override
    public Account getAccountByOwnersEmail(Email recipientEmail) {
        return accountQueryService.getAccountByOwnersEmail(recipientEmail);
    }

    /**
     * Retrieves an account by its IBAN provided as a String.
     *
     * @param iban the IBAN of the account as a String
     * @return the account associated with the specified IBAN
     *
     * @deprecated Use {@link #getAccountByIban(Iban)} instead for enhanced type safety.
     */
    @Deprecated
    @Override
    public Account getAccountByIban(String iban) {
        return accountQueryService.getAccountByIban(iban);
    }

    /**
     * Retrieves an account by its IBAN.
     *
     * Delegates the query to the account query service to locate and return the account associated with the given IBAN.
     *
     * @param iban the IBAN representing the international bank account number to search for
     * @return the account corresponding to the provided IBAN
     */
    @Override
    public Account getAccountByIban(Iban iban) {
        return accountQueryService.getAccountByIban(iban);
    }

    /**
     * Checks whether an account exists for the specified email address.
     *
     * @param email the email address to check for an associated account
     * @return true if an account with the given email exists, false otherwise
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