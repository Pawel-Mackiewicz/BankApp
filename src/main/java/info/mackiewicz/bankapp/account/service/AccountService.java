package info.mackiewicz.bankapp.account.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.AccountFactory;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.account.service.interfaces.AccountServiceInterface;
import info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.shared.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService implements AccountServiceInterface {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final AccountFactory accountFactory;
    private final AccountOperationsService accountOperationsService;
    private final AccountQueryService accountQueryService;

    /**
     * Creates a new bank account for a specified user.
     * This method uses AccountFactory to create the account and then saves it to
     * the repository.
     *
     * @param userId The unique identifier of the user who will own the account
     * @return A newly created {@link Account} instance
     * @throws IllegalArgumentException if userId is null
     */
    @Override
    @Transactional
    public Account createAccount(@NotNull Integer userId) {
        log.debug("Creating account for user ID: {}", userId);

        User user = userService.getUserById(userId);
        Account account = accountFactory.createAccount(user);
        return accountRepository.save(account);
    }

    /**
     * Retrieves an account by its unique identifier.
     * This method delegates to AccountQueryService.
     *
     * @param id The unique identifier of the account to retrieve
     * @return The {@link Account} matching the specified id
     * @throws AccountNotFoundByIdException if no account is found with the given id
     */
    @Override
    public Account getAccountById(int id) {
        return accountQueryService.getAccountById(id);
    }

    /**
     * Retrieves all accounts in the system.
     * This method delegates to AccountQueryService.
     *
     * @return A list of all {@link Account}s currently in the system
     */
    @Override
    public List<Account> getAllAccounts() {
        return accountQueryService.getAllAccounts();
    }
    
    /**
     * Retrieves all accounts associated with an owner's PESEL number.
     * This method delegates to AccountQueryService.
     *
     * @param pesel The PESEL number to search for
     * @return A list of {@link Account}s belonging to the owner with the specified
     *         PESEL
     * @throws OwnerAccountsNotFoundException if no accounts are found for the given
     *                                        PESEL
     */
    @Override
    public List<Account> getAccountsByOwnersPESEL(String pesel) {
        return accountQueryService.getAccountsByOwnersPESEL(pesel);
    }

    /**
     * Retrieves accounts based on the owner's username.
     * This method delegates to AccountQueryService.
     *
     * @param username The username to search for
     * @return A list of {@link Account}s belonging to the owner with the specified
     *         username
     * @throws OwnerAccountsNotFoundException if no accounts are found for the given
     *                                        username
     */
    @Override
    public List<Account> getAccountsByOwnersUsername(String username) {
        return accountQueryService.getAccountsByOwnersUsername(username);
    }
    
    /**
     * Retrieves all accounts associated with the owner's unique identifier.
     * This method delegates to AccountQueryService.
     *
     * @param id The unique identifier of the account owner
     * @return A list of {@link Account}s belonging to the owner with the specified
     *         ID
     * @throws OwnerAccountsNotFoundException if no accounts are found for the given
     *                                        owner ID
     */
    @Override
    public List<Account> getAccountsByOwnersId(Integer id) {
        return accountQueryService.getAccountsByOwnersId(id);
    }
    
    /**
     * Finds an account by the owner's email address.
     * This method delegates to AccountQueryService.
     *
     * @param recipientEmail The email address to search for
     * @return An {@link Optional} containing the account if found, or empty if no
     *         account is associated with the email
     */
    @Override
    public Account findAccountByOwnersEmail(@Email(message = "Invalid email format") String recipientEmail) {
        return accountQueryService.findAccountByOwnersEmail(recipientEmail);
    }
    
        /**
         * Finds an account by its IBAN (International Bank Account Number).
         * This method delegates to AccountQueryService.
         *
         * @param iban The IBAN to search for
         * @return An {@link Optional} containing the account if found, or empty if no
         *         account matches the IBAN
         */
        @Override
        public Account findAccountByIban(String iban) {
            return accountQueryService.findAccountByIban(iban);
        }
    
    /**
     * Deletes an account with the specified ID.
     * This method uses AccountQueryService to retrieve the account and then deletes
     * it.
     *
     * @param id The unique identifier of the account to delete
     * @throws AccountNotFoundByIdException if no account is found with the given id
     */
    @Override
    @Transactional
    public void deleteAccountById(int id) {
        log.debug("Deleting account with ID: {}", id);
        Account account = getAccountById(id);
        accountRepository.delete(account);
    }
    
    /**
     * Deposits funds into an account.
     * This method delegates to AccountOperationsService.
     *
     * @param account The account to deposit into
     * @param amount  The amount to deposit
     * @return The updated account after the deposit
     */
    @Override
    @Transactional
    public Account deposit(Account account,
    @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        return accountOperationsService.deposit(account, amount);
    }

    /**
     * Withdraws funds from an account.
     * This method delegates to AccountOperationsService.
     *
     * @param account The account to withdraw from
     * @param amount  The amount to withdraw
     * @return The updated account after the withdrawal
     */
    @Override
    @Transactional
    public Account withdraw(Account account,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        return accountOperationsService.withdraw(account, amount);
    }
}