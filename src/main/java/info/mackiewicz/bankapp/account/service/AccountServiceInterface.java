package info.mackiewicz.bankapp.account.service;

import jakarta.validation.constraints.Email;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import info.mackiewicz.bankapp.account.model.Account;

/**
 * Interfejs definiujący operacje na kontach bankowych.
 */
public interface AccountServiceInterface {
    /**
     * Tworzy nowe konto dla użytkownika.
     */
    Account createAccount(Integer userId);

    /**
     * Pobiera konto po jego ID.
     */
    Account getAccountById(int id);

    /**
     * Pobiera konta na podstawie dowolnego kryterium właściciela.
     */
    List<Account> getAccountsByOwnerCriteria(String value, Function<String, Optional<List<Account>>> finder, String criteriaName);

    /**
     * Pobiera konta na podstawie numeru PESEL właściciela.
     */
    List<Account> getAccountsByOwnersPESEL(String pesel);

    /**
     * Pobiera konta na podstawie nazwy użytkownika właściciela.
     */
    List<Account> getAccountsByOwnersUsername(String username);

    /**
     * Pobiera konta na podstawie ID właściciela.
     */
    List<Account> getAccountsByOwnersId(Integer id);

    /**
     * Pobiera wszystkie konta w systemie.
     */
    List<Account> getAllAccounts();

    /**
     * Wyszukuje konto po numerze IBAN.
     */
    Optional<Account> findAccountByIban(String iban);

    /**
     * Wyszukuje konto po adresie email właściciela.
     */
    Optional<Account> findAccountByOwnersEmail(@Email(message = "Invalid email format") String recipientEmail);

    /**
     * Usuwa konto o podanym ID.
     */
    void deleteAccountById(int id);

    /**
     * Zmienia właściciela konta.
     */
    Account changeAccountOwner(int accountId, int newOwnerId);

    /**
     * Wpłaca środki na konto.
     */
    Account deposit(int accountId, BigDecimal amount);

    /**
     * Wypłaca środki z konta.
     */
    Account withdraw(int accountId, BigDecimal amount);
}