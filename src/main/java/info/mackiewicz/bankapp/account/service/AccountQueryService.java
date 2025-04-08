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
     * Retrieves a list of accounts associated with the specified owner ID.
     *
     * <p>This method converts the provided numeric ID into a String and delegates the search
     * to {@code getAccountsByOwnerCriteria} using "ID" as the criteria name.</p>
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
     * Retrieves the first account associated with the given owner's email.
     * <p>
     * Searches for an account corresponding to the specified email and returns it if found.
     * If no account is linked to the email, an {@link OwnerAccountsNotFoundException} is thrown.
     * </p>
     *
     * @param recipientEmail the email of the account owner
     * @return the account associated with the given email
     * @throws OwnerAccountsNotFoundException if no account is found for the specified email
     */
    Account getAccountByOwnersEmail(Email recipientEmail) {
        log.debug("Finding account by owner's email: {}", recipientEmail);
        return accountRepository.findFirstByOwner_email(recipientEmail)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with email %s does not have any account.", recipientEmail)));
    }

    /**
     * Retrieves the account associated with the given email address by converting the string into an {@code Email} object.
     *
     * <p>This method is deprecated and will be removed in future versions. Use {@link #getAccountByOwnersEmail(Email)}
     * instead for a type-safe approach.
     *
     * @param email the email address as a string to be converted into an {@code Email} object
     * @return the account associated with the provided email address
     * @deprecated Use {@link #getAccountByOwnersEmail(Email)} instead.
     */
    @Deprecated
    Account findAccountByOwnersEmail(String email) {
        return getAccountByOwnersEmail(new Email(email));
    }

    /**
     * Retrieves the account associated with the provided IBAN.
     *
     * <p>This method converts the IBAN from its string representation into an Iban object and then delegates the
     * lookup to {@link #getAccountByIban(Iban)}. An AccountNotFoundByIbanException is thrown if no matching account is found.</p>
     *
     * @param iban the IBAN in string format
     * @return the account corresponding to the given IBAN
     */
    Account getAccountByIban(String iban) {
        return getAccountByIban(Iban.valueOf(iban));
    }

    /**
     * Retrieves an account by its IBAN.
     *
     * @param iban the IBAN used to locate the account
     * @return the account associated with the specified IBAN
     * @throws AccountNotFoundByIbanException if no account is found with the given IBAN
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