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
     * Retrieves accounts associated with the specified owner's ID.
     *
     * <p>This method converts the owner's ID to a string and uses it as the search criterion to query
     * the repository for accounts linked to the owner. If no matching accounts are found, an
     * OwnerAccountsNotFoundException is thrown.
     *
     * @param id the unique identifier of the account owner
     * @return a list of accounts belonging to the owner
     * @throws OwnerAccountsNotFoundException if no accounts are found for the given owner ID
     */
    List<Account> getAccountsByOwnersId(Integer id) {
        return getAccountsByOwnerCriteria(id.toString(),
                value -> accountRepository.findAccountsByOwner_id(Integer.parseInt(value)),
                "ID");
    }

    /**
     * Retrieves the first account associated with the specified owner's email.
     *
     * @param recipientEmail the email address of the account owner
     * @return the account linked to the provided email
     * @throws OwnerAccountsNotFoundException if no account is found for the given email
     */
    Account getAccountByOwnersEmail(Email recipientEmail) {
        log.debug("Finding account by owner's email: {}", recipientEmail);
        return accountRepository.findFirstByOwner_email(recipientEmail)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with email %s does not have any account.", recipientEmail)));
    }

    /**
     * Retrieves an account by the owner's email address.
     *
     * <p>This method is deprecated. It converts the provided email string to an {@link Email}
     * object and delegates account retrieval to {@link #getAccountByOwnersEmail(Email)}.
     *
     * @param email the owner's email address in string format
     * @return the account associated with the given email address
     * @deprecated Use {@link #getAccountByOwnersEmail(Email)} instead.
     */
    @Deprecated
    Account findAccountByOwnersEmail(String email) {
        return getAccountByOwnersEmail(new Email(email));
    }

    /**
     * Retrieves an account by its IBAN provided as a String.
     *
     * <p>This method converts the provided IBAN string into an {@code Iban} object and delegates
     * to {@link #getAccountByIban(Iban)} to retrieve the corresponding account.
     *
     * @param iban the IBAN of the account as a String
     * @return the account associated with the specified IBAN
     * @throws AccountNotFoundByIbanException if no account exists for the given IBAN
     */
    Account getAccountByIban(String iban) {
        return getAccountByIban(Iban.valueOf(iban));
    }

    /**
     * Retrieves an account using the specified IBAN.
     *
     * <p>If no account is found corresponding to the given IBAN, an
     * {@link AccountNotFoundByIbanException} is thrown.</p>
     *
     * @param iban the IBAN for the account lookup
     * @return the account associated with the provided IBAN
     * @throws AccountNotFoundByIbanException if an account with the specified IBAN does not exist
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