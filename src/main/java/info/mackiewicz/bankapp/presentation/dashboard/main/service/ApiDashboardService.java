package info.mackiewicz.bankapp.presentation.dashboard.main.service;

import info.mackiewicz.bankapp.core.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.core.account.repository.AccountRepository;
import info.mackiewicz.bankapp.core.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiDashboardService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Calculates the working balance of an account by subtracting the amount on hold
     * from the available account balance. The operation retrieves the account's balance
     * from the repository, checks for its existence, and deducts the total hold amount
     * for pending or new transactions.
     *
     * @param accountId the unique identifier of the account for which the working balance is calculated
     *
     * @return the calculated working balance as a BigDecimal
     * @throws AccountNotFoundByIdException when no account is found with the provided accountId
     */
    public BigDecimal getWorkingBalance(int accountId) {
        log.debug("Calculating working balance for account with ID: {}", accountId);

        log.trace("Retrieving account balance for account with ID: {}", accountId);
        BigDecimal balance = accountRepository.findBalanceById(accountId)
                .orElseThrow(
                        () -> new AccountNotFoundByIdException("Account with ID " + accountId + " not found.")
                );

        log.trace("Retrieving amount on hold for account with ID: {}", accountId);
        BigDecimal balanceOnHold = transactionRepository.findBalanceOnHoldBySourceAccount_Id(accountId);
        BigDecimal workingBalance = balance.subtract(balanceOnHold);

        if (workingBalance.signum() < 0) {
            log.warn("Working balance get below 0.");
        }
        return workingBalance;
    }
}
