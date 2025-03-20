package info.mackiewicz.bankapp.account.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.account.service.interfaces.AccountServiceInterface;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService implements AccountServiceInterface {

    private final AccountRepository accountRepository;
    private final AccountOperationsService accountOperationsService;
    private final AccountQueryService accountQueryService;
    private final AccountCreationService accountCreationService;

    /**
     * Creates a new account for the specified user.
     * <p>
     * This method attempts to create an account and will retry if it fails,
     * up to a maximum number of retries with increasing delays between attempts.
     * </p>
     *
     * @param userId The ID of the user who will own the account
     * @return The newly created account
     * @throws RuntimeException if account creation fails after all retry attempts
     */
    @Override
    @Transactional
    public Account createAccount(@NotNull Integer userId) {
        log.debug("Delegating account creation to AccountCreationService for user ID: {}", userId);
        return accountCreationService.createAccount(userId);
    }

    @Override
    public Account getAccountById(int id) {
        return accountQueryService.getAccountById(id);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountQueryService.getAllAccounts();
    }

    @Override
    public List<Account> getAccountsByOwnersPesel(String pesel) {
        return accountQueryService.getAccountsByOwnersPesel(pesel);
    }

    @Override
    public List<Account> getAccountsByOwnersUsername(String username) {
        return accountQueryService.getAccountsByOwnersUsername(username);
    }

    @Override
    public List<Account> getAccountsByOwnersId(Integer id) {
        return accountQueryService.getAccountsByOwnersId(id);
    }

    @Override
    public Account findAccountByOwnersEmail(@Email(message = "Invalid email format") String recipientEmail) {
        return accountQueryService.findAccountByOwnersEmail(recipientEmail);
    }

    @Override
    public Account findAccountByIban(String iban) {
        log.debug("inside AccountService findAccountByIban");
        return accountQueryService.findAccountByIban(iban);
    }

    public boolean existsByEmail(@Email(message = "Invalid email format") String email) {
        return accountQueryService.existsByEmail(email);
    }

    @Override
    @Transactional
    public void deleteAccountById(int id) {
        log.debug("Deleting account with ID: {}", id);
        Account account = getAccountById(id);
        accountRepository.delete(account);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Account deposit(Account account,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        return accountOperationsService.deposit(account, amount);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Account withdraw(Account account,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        return accountOperationsService.withdraw(account, amount);
    }
}