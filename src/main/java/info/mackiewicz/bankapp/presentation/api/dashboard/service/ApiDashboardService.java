package info.mackiewicz.bankapp.presentation.api.dashboard.service;

import info.mackiewicz.bankapp.core.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.core.account.repository.AccountRepository;
import info.mackiewicz.bankapp.core.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
        BigDecimal balance = accountRepository.findBalanceById(accountId)
                .orElseThrow(
                        () -> new AccountNotFoundByIdException("Account with ID " + accountId + " not found.")
                );
        BigDecimal balanceOnHold = transactionRepository.findBalanceOnHoldBySourceAccount_Id(accountId);

        return balance.subtract(balanceOnHold);
    }
}
