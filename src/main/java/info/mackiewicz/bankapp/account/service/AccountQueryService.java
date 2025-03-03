package info.mackiewicz.bankapp.account.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.shared.exception.OwnerAccountsNotFoundException;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for querying and retrieving account information.
 * <p>
 * This service provides methods for finding accounts by various criteria
 * such as ID, IBAN, owner's email, username, PESEL, etc.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AccountQueryService {

    private final AccountRepository accountRepository;

    /**
     * Retrieves an account by its unique identifier.
     *
     * @param id The unique identifier of the account to retrieve
     * @return The {@link Account} matching the specified id
     * @throws AccountNotFoundByIdException if no account is found with the given id
     */
    public Account getAccountById(int id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundByIdException("Account with ID " + id + " not found."));
    }

    /**
     * Retrieves all accounts in the system.
     *
     * @return A list of all {@link Account}s currently in the system
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    /**
     * Retrieves accounts based on a flexible owner criteria using a custom finder
     * function.
     * This method provides a generic way to search for accounts based on any
     * owner-related criteria.
     *
     * @param value        The search value to match against owner criteria
     * @param finder       A function that implements the specific search logic
     * @param criteriaName The name of the criteria being searched (for
     *                     logging/error reporting)
     * @return A list of {@link Account}s matching the specified criteria
     * @throws OwnerAccountsNotFoundException if no accounts are found matching the
     *                                        criteria
     */
    public List<Account> getAccountsByOwnerCriteria(String value, Function<String, Optional<List<Account>>> finder,
            String criteriaName) {
        return finder.apply(value)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with %s %s does not have any account.", criteriaName, value)));
    }

    /**
     * Retrieves all accounts associated with an owner's PESEL number.
     * PESEL is a unique national identification number in Poland.
     *
     * @param pesel The PESEL number to search for
     * @return A list of {@link Account}s belonging to the owner with the specified
     *         PESEL
     * @throws OwnerAccountsNotFoundException if no accounts are found for the given
     *                                        PESEL
     */
    public List<Account> getAccountsByOwnersPESEL(String pesel) {
        return getAccountsByOwnerCriteria(pesel, accountRepository::findAccountsByOwner_PESEL, "PESEL");
    }

    /**
     * Retrieves accounts based on the owner's username.
     *
     * @param username The username to search for
     * @return A list of {@link Account}s belonging to the owner with the specified
     *         username
     * @throws OwnerAccountsNotFoundException if no accounts are found for the given
     *                                        username
     */
    public List<Account> getAccountsByOwnersUsername(String username) {
        return getAccountsByOwnerCriteria(username, accountRepository::findAccountsByOwner_username, "username");
    }

    /**
     * Retrieves all accounts associated with the owner's unique identifier.
     *
     * @param id The unique identifier of the account owner
     * @return A list of {@link Account}s belonging to the owner with the specified
     *         ID
     * @throws OwnerAccountsNotFoundException if no accounts are found for the given
     *                                        owner ID
     */
    public List<Account> getAccountsByOwnersId(Integer id) {
        return getAccountsByOwnerCriteria(id.toString(),
                value -> accountRepository.findAccountsByOwner_id(Integer.parseInt(value)),
                "ID");
    }

    /**
     * Finds an account by the owner's email address.
     *
     * @param recipientEmail The email address to search for
     * @return An {@link Optional} containing the account if found, or empty if no
     *         account is associated with the email
     */
    public Account findAccountByOwnersEmail(@Email(message = "Invalid email format") String recipientEmail) {
        log.debug("Finding account by owner's email: {}", recipientEmail);
        return accountRepository.findFirstByOwner_email(recipientEmail)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with email %s does not have any account.", recipientEmail)));
    }

    /**
     * Finds an account by its IBAN (International Bank Account Number).
     *
     * @param iban The IBAN to search for
     * @return An {@link Optional} containing the account if found, or empty if no
     *         account matches the IBAN
     */
    public Account findAccountByIban(String iban) {

        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundByIbanException("Account with IBAN " + iban + " not found."));
    }

    /**
     * Finds an account by its IBAN (International Bank Account Number).
     *
     * @param iban The IBAN to search for
     * @return An {@link Optional} containing the account if found, or empty if no
     *         account matches the IBAN
     */
    public Account findAccountByIban(Iban iban) {
        return findAccountByIban(iban.toString());
    }
}