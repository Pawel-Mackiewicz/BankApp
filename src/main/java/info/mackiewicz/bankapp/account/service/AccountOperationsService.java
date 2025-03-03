package info.mackiewicz.bankapp.account.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for financial operations on accounts.
 * <p>
 * This service handles deposit and withdrawal operations, ensuring proper
 * validation
 * and transaction management.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class AccountOperationsService {

    private final AccountRepository accountRepository;
    private final AccountValidationService validationService;

    /**
     * Deposits funds into an account.
     *
     * @param account The account to deposit into
     * @param amount  The amount to deposit
     * @return The updated account after the deposit
     */
    @Transactional
    Account deposit(Account account,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {

        validationService.validateDeposit(amount);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    /**
     * Withdraws funds from an account.
     *
     * @param account The account to withdraw from
     * @param amount  The amount to withdraw
     * @return The updated account after the withdrawal
     */
    @Transactional
    Account withdraw(Account account,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {

        validationService.validateWithdrawal(account.getBalance(), amount);
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }
}
