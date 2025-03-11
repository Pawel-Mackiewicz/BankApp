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
import info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.shared.exception.OwnerAccountsNotFoundException;
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

    List<Account> getAccountsByOwnersId(Integer id) {
        return getAccountsByOwnerCriteria(id.toString(),
                value -> accountRepository.findAccountsByOwner_id(Integer.parseInt(value)),
                "ID");
    }

    Account findAccountByOwnersEmail(Email recipientEmail) {
        log.debug("Finding account by owner's email: {}", recipientEmail);
        return accountRepository.findFirstByOwner_email(recipientEmail)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with email %s does not have any account.", recipientEmail)));
    }

    Account findAccountByOwnersEmail(String email) {
        return findAccountByOwnersEmail(new Email(email));
    }

    Account findAccountByIban(String iban) {
        return findAccountByIban(Iban.valueOf(iban));
    }

    Account findAccountByIban(Iban iban) {
        log.debug("Finding account by IBAN: {}", iban.toFormattedString());
        return accountRepository.findByIban(iban)
        .orElseThrow(() -> new AccountNotFoundByIbanException("Account with IBAN " + iban.toFormattedString() + " not found."));
    }
}