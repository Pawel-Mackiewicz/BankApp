package info.mackiewicz.bankapp.core.account.service;

import info.mackiewicz.bankapp.core.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.core.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.core.account.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.repository.AccountRepository;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.model.vo.Pesel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import java.util.List;

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

    Account getAccountByIban(Iban iban) {
        log.debug("Finding account by IBAN: {}", iban.toFormattedString());
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundByIbanException("Account with IBAN " + iban.toFormattedString() + " not found."));
    }

    Account getAccountByOwnersEmail(EmailAddress recipientEmail) {
        log.debug("Finding account by owner's email: {}", recipientEmail);
        return accountRepository.findFirstByOwner_email(recipientEmail)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with email %s does not have any account.", recipientEmail)));
    }

    List<Account> getAccountsByOwnersId(Integer id) {
        return accountRepository.findAccountsByOwner_id(id)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with ID: %s does not have any account.", id)));
    }

    List<Account> getAccountsByOwnersPesel(Pesel pesel) {
        return accountRepository.findAccountsByOwner_pesel(pesel)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with PESEL %s does not have any account.", pesel)));
    }

    List<Account> getAccountsByOwnersUsername(String username) {
        return accountRepository.findAccountsByOwner_username(username)
                .orElseThrow(() -> new OwnerAccountsNotFoundException(
                        String.format("User with username: %s does not have any account.", username)));
    }

    List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    boolean existsByEmail(EmailAddress email) {
        log.debug("Checking if account exists by email: {}", email);
        return accountRepository.existsByOwner_email(email);
    }
}