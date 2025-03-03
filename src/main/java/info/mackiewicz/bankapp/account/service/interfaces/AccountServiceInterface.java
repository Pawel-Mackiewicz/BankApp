package info.mackiewicz.bankapp.account.service.interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import info.mackiewicz.bankapp.account.model.Account;
import jakarta.validation.constraints.Email;

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
     * @throws info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIdException if no account is found with the given id
     */
    Account getAccountById(int id);

    /**
     * Retrieves accounts based on a flexible owner criteria using a custom finder function.
     * This method provides a generic way to search for accounts based on any owner-related criteria.
     *
     * @param value The search value to match against owner criteria
     * @param finder A function that implements the specific search logic
     * @param criteriaName The name of the criteria being searched (for logging/error reporting)
     * @return A list of {@link Account}s matching the specified criteria
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    List<Account> getAccountsByOwnerCriteria(String value, Function<String, Optional<List<Account>>> finder, String criteriaName);

    /**
     * Retrieves all accounts associated with an owner's PESEL number.
     * PESEL is a unique national identification number in Poland.
     *
     * @param pesel The PESEL number to search for
     * @return A list of {@link Account}s belonging to the owner with the specified PESEL
     * @throws IllegalArgumentException if pesel is null or invalid
     */
    List<Account> getAccountsByOwnersPESEL(String pesel);

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
     * @return An {@link Optional} containing the account if found, or empty if no account matches the IBAN
     * @throws IllegalArgumentException if iban is null or empty
     */
    Optional<Account> findAccountByIban(String iban);

    /**
     * Finds an account by the owner's email address.
     *
     * @param recipientEmail The email address to search for
     * @return An {@link Optional} containing the account if found, or empty if no account is associated with the email
     * @throws IllegalArgumentException if recipientEmail is null or has invalid format
     */
    Optional<Account> findAccountByOwnersEmail(@Email(message = "Invalid email format") String recipientEmail);

    /**
     * Deletes an account with the specified ID.
     *
     * @param id The unique identifier of the account to delete
     * @throws info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIdException if no account is found with the given id
     */
    void deleteAccountById(int id);

    /**
     * Deposits funds into an account.
     *
     * @param accountId The unique identifier of the account to deposit into
     * @param amount The amount to deposit
     * @return The updated {@link Account} after the deposit
     * @throws IllegalArgumentException if accountId is null or amount is negative
     */
    Account deposit(Account account, BigDecimal amount);

    /**
     * Withdraws funds from an account.
     *
     * @param accountId The unique identifier of the account to withdraw from
     * @param amount The amount to withdraw
     * @return The updated {@link Account} after the withdrawal
     * @throws IllegalArgumentException if accountId is null or amount is negative
     */
    Account withdraw(Account account, BigDecimal amount);
}