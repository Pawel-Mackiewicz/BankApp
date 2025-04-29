package info.mackiewicz.bankapp.core.account.service.interfaces;

import info.mackiewicz.bankapp.core.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.core.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.core.account.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.model.vo.Pesel;
import org.iban4j.Iban;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface defining bank account operations for managing accounts, balances and owner-related queries.
 * Provides functionality for creating, retrieving, updating and deleting bank accounts,
 * as well as performing financial operations like deposits and withdrawals.
 */
public interface AccountServiceInterface {

    /**
     * Creates a new bank account for a specified user.
     *
     * @param userId The unique identifier of the user who will own the account
     * @return A newly created {@link Account} instance
     * @throws IllegalArgumentException if userId is null
     */
    Account createAccount(Integer userId);

    /**
     * Retrieves an account by its unique identifier.
     *
     * @param id The unique identifier of the account to retrieve
     * @return The {@link Account} matching the specified id
     * @throws AccountNotFoundByIdException if no account is found with the given id
     */
    Account getAccountById(int id);

    /**
     * Retrieves all accounts associated with an owner's PESEL number.
     * PESEL is a unique national identification number in Poland.
     *
     * @param pesel The PESEL number to search for
     * @return A list of {@link Account}s belonging to the owner with the specified PESEL
     * @throws IllegalArgumentException if pesel is null or invalid
     */
    @Deprecated
    List<Account> getAccountsByOwnersPesel(String pesel);


    /**
     * Retrieves all accounts associated with an owner's PESEL number.
     * PESEL is a unique national identification number in Poland.
     *
     * @param pesel The PESEL number to search for
     *
     * @return A list of {@link Account}s belonging to the owner with the specified PESEL
     * @throws IllegalArgumentException if pesel is null or invalid
     * @see Pesel
     */
    List<Account> getAccountsByOwnersPesel(Pesel pesel);

    /**
     * Retrieves accounts based on the owner's username.
     *
     * @param username The username to search for
     * @return A list of {@link Account}s belonging to the owner with the specified username
     * @throws IllegalArgumentException if username is null or empty
     */
    List<Account> getAccountsByOwnersUsername(String username);

    /**
     * Retrieves all accounts associated with the owner's unique identifier.
     *
     * @param id The unique identifier of the account owner
     * @return A list of {@link Account}s belonging to the owner with the specified ID
     * @throws IllegalArgumentException if id is null
     */
    List<Account> getAccountsByOwnersId(Integer id);

    /**
     * Retrieves all accounts in the system.
     *
     * @return A list of all {@link Account}s currently in the system
     */
    List<Account> getAllAccounts();

    /**
     * Finds an account by its IBAN (International Bank Account Number).
     *
     * @param iban The IBAN to search for
     * @return An {@link Account} with the specified IBAN
     * @throws IllegalArgumentException if iban is null or empty
     * @deprecated Use {@link #getAccountByIban(Iban)} instead.
     */
    @Deprecated
    Account getAccountByIban(String iban);

    /**
     * Finds an account by its IBAN (International Bank Account Number).
     *
     * @param iban The IBAN to search for
     * @return An {@link Account} with the specified IBAN
     * @throws AccountNotFoundByIbanException if no account is found with the given IBAN
     */
    Account getAccountByIban(Iban iban);

    /**
     * Finds an account by the owner's email address.
     *
     * @param recipientEmail The email address to search for
     * @return An {@link Account} belonging to the owner with the specified email address
     * @throws IllegalArgumentException if recipientEmail is null or has invalid format
     */
    Account getAccountByOwnersEmail(@jakarta.validation.constraints.Email(message = "Invalid email format") String recipientEmail);

    /**
     * Finds an account by the owner's email address.
     *
     * @param recipientEmail The email address to search for
     * @return An {@link Account} belonging to the owner with the specified email address
     * @throws OwnerAccountsNotFoundException if no account is found with the given email
     */
    Account getAccountByOwnersEmail(EmailAddress recipientEmail);

        /**
         * Deletes an account with the specified ID.
         *
         * @param id The unique identifier of the account to delete
         * @throws AccountNotFoundByIdException if no account is found with the given id
         */
        void deleteAccountById(int id);

        /**
         * Checks if an account exists by the owner's email address.
         *
         * @param email The email address to check
         * @return true if an account exists with the specified email, false otherwise
         * @throws IllegalArgumentException if email is null or has invalid format
         */
        boolean existsByEmail(EmailAddress email);

        /**
         * Deposits funds into an account.
         *
         * @param account The account to deposit funds into
         * @param amount The amount to deposit
         * @return The updated {@link Account} after the deposit
         * @throws IllegalArgumentException if account is null or amount is negative
         */
        Account deposit(Account account, BigDecimal amount);

        /**
         * Withdraws funds from an account.
         *
         * @param account The account to withdraw funds from
         * @param amount The amount to withdraw
         * @return The updated {@link Account} after the withdrawal
         * @throws IllegalArgumentException if account is null or amount is negative
         */
        Account withdraw(Account account, BigDecimal amount);
    }