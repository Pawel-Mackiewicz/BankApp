package info.mackiewicz.bankapp.account.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for financial operations on accounts.
 * <p>
 * This service handles deposit and withdrawal operations, ensuring proper
 * validation
 * and transaction management.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
class AccountOperationsService {

    private final AccountRepository accountRepository;
    private final AccountValidationService validationService;

    Account deposit(Account account,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        log.debug("Depositing {} to account {}", amount, account.getId());
        validationService.validateDeposit(amount);
        account.setBalance(account.getBalance().add(amount));
        log.debug("Deposit successful. New balance: {}", account.getBalance());
        return accountRepository.save(account);
    }

    Account withdraw(Account account,
            @NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) {
        log.debug(null, "Withdrawing {} from account {}", amount, account.getId());
        validationService.validateWithdrawal(account.getBalance(), amount);
        account.setBalance(account.getBalance().subtract(amount));
        log.debug("Withdrawal successful. New balance: {}", account.getBalance());
        return accountRepository.save(account);
    }
}
