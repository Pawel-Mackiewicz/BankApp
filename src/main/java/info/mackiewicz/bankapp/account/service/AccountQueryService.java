package info.mackiewicz.bankapp.account.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.account.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
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
class AccountQueryService {

    private final AccountRepository accountRepository;

    Account getAccountById(int id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundByIdException("Account with ID " + id + " not found."));
    }

    List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    List<Account> getAccountsByOwnerCriteria(String value, Function<String, Optional<List<Account>>> finder,
            String criteriaName) {
        return finder.apply(value)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with %s %s does not have any account.", criteriaName, value)));
    }

    List<Account> getAccountsByOwnersPesel(Pesel pesel) {
        return accountRepository.findAccountsByOwner_pesel(pesel)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with PESEL %s does not have any account.", pesel)));
    }

    List<Account> getAccountsByOwnersPesel(String pesel) {
        return getAccountsByOwnersPesel(new Pesel(pesel));
    }

    List<Account> getAccountsByOwnersUsername(String username) {
        return getAccountsByOwnerCriteria(username, accountRepository::findAccountsByOwner_username, "username");
    }

    /**
     * Retrieves all accounts associated with the specified owner ID.
     *
     * This method converts the provided owner ID to a string and delegates the search to a criteria-based query,
     * using the criteria name "ID" to locate accounts by their owner's ID.
     *
     * @param id the unique identifier of the account owner
     * @return a list of accounts belonging to the specified owner
     * @throws OwnerAccountsNotFoundException if no accounts are found for the given owner ID
     */
    List<Account> getAccountsByOwnersId(Integer id) {
        return getAccountsByOwnerCriteria(id.toString(),
                value -> accountRepository.findAccountsByOwner_id(Integer.parseInt(value)),
                "ID");
    }

    /**
     * Retrieves the account associated with the provided owner's email.
     *
     * This method searches for the first account linked to the specified email using the account repository.
     * If no account is found, it throws an OwnerAccountsNotFoundException.
     *
     * @param recipientEmail the email identifying the account owner
     * @return the account corresponding to the specified email
     * @throws OwnerAccountsNotFoundException if no account exists for the provided email
     */
    Account getAccountByOwnersEmail(Email recipientEmail) {
        log.debug("Finding account by owner's email: {}", recipientEmail);
        return accountRepository.findFirstByOwner_email(recipientEmail)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with email %s does not have any account.", recipientEmail)));
    }

    /**
     * Deprecated method that retrieves an account using the owner's email provided as a string.
     * <p>
     * This method converts the provided string into an Email object and delegates the lookup to
     * {@link #getAccountByOwnersEmail(Email)}.
     *
     * @param email the string representation of the owner's email
     * @return the account associated with the given email
     * @deprecated Use {@link #getAccountByOwnersEmail(Email)} instead.
     */
    @Deprecated
    Account findAccountByOwnersEmail(String email) {
        return getAccountByOwnersEmail(new Email(email));
    }

    /**
     * Retrieves an account based on its IBAN.
     *
     * <p>This method converts the provided IBAN string into an {@code Iban} object and delegates
     * the account lookup to the {@link #getAccountByIban(Iban)} method.</p>
     *
     * @param iban the IBAN of the account as a string
     * @return the account associated with the specified IBAN
     * @throws AccountNotFoundByIbanException if no account is found for the given IBAN
     */
    Account getAccountByIban(String iban) {
        return getAccountByIban(Iban.valueOf(iban));
    }

    /**
     * Retrieves an account associated with the specified IBAN.
     * <p>
     * Searches for an account in the repository using the provided IBAN. If no matching account
     * is found, an AccountNotFoundByIbanException is thrown.
     * </p>
     *
     * @param iban the IBAN used to locate the account
     * @return the account associated with the given IBAN
     * @throws AccountNotFoundByIbanException if no account with the specified IBAN exists
     */
    Account getAccountByIban(Iban iban) {
        log.debug("Finding account by IBAN: {}", iban.toFormattedString());
        return accountRepository.findByIban(iban)
        .orElseThrow(() -> new AccountNotFoundByIbanException("Account with IBAN " + iban.toFormattedString() + " not found."));
    }

    boolean existsByEmail(Email email) {
        log.debug("Checking if account exists by email: {}", email);
        return accountRepository.existsByOwner_email(email);
    }

    boolean existsByEmail(String email) {
        return existsByEmail(new Email(email));
    }

}