package info.mackiewicz.bankapp.account.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.AccountFactory;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.account.service.interfaces.AccountServiceInterface;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService implements AccountServiceInterface {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final AccountFactory accountFactory;
    private final AccountOperationsService accountOperationsService;
    private final AccountQueryService accountQueryService;

    @Override
    @Transactional
    public Account createAccount(@NotNull Integer userId) {
        log.debug("Creating account for user ID: {}", userId);

        User user = userService.getUserById(userId);
        Account account = accountFactory.createAccount(user);
        return accountRepository.save(account);
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
    public List<Account> getAccountsByOwnersPESEL(String pesel) {
        return accountQueryService.getAccountsByOwnersPESEL(pesel);
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
        return accountQueryService.findAccountByIban(iban);
    }

    @Override
    @Transactional
    public void deleteAccountById(int id) {
        log.debug("Deleting account with ID: {}", id);
        Account account = getAccountById(id);
        accountRepository.delete(account);
    }

    @Override
    @Transactional
    public Account deposit(Account account,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        return accountOperationsService.deposit(account, amount);
    }

    @Override
    @Transactional
    public Account withdraw(Account account,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        return accountOperationsService.withdraw(account, amount);
    }
}