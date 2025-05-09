package info.mackiewicz.bankapp.presentation.dashboard.service;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.service.TransactionService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.DashboardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DashboardService {
    private final AccountService accountService;
    private final TransactionService transactionService;


    public DashboardDTO getDashboardData(Integer userId) {
        List<Account> accounts = getAccountsByUserId(userId);
        return buildDashboardDTO(accounts, userId);
    }

    private List<Account> getAccountsByUserId(Integer userId) {
        return accountService.getAccountsByOwnersId(userId);
    }

    private DashboardDTO buildDashboardDTO(List<Account> accounts, Integer userId) {
        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setUserId(userId);
        dashboard.setAccounts(accounts);
        dashboard.setRecentTransactions(getRecentTransactions(accounts));
        dashboard.setTotalBalance(calculateTotalBalance(accounts));

        setPrimaryAccountInfo(dashboard, accounts);
        return dashboard;
    }

    private List<Transaction> getRecentTransactions(List<Account> accounts) {
        return accounts.stream()
                .map(Account::getId)
                .flatMap(id -> transactionService.getRecentTransactions(id, 5).stream())
                .distinct()
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .limit(5)
                .toList();
    }

    private BigDecimal calculateTotalBalance(List<Account> accounts) {
        return accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void setPrimaryAccountInfo(DashboardDTO dashboard, List<Account> accounts) {
        if (!accounts.isEmpty()) {
            Account primaryAccount = accounts.get(0);
            dashboard.setAccountName("Primary Account");
            dashboard.setAccountNumber(primaryAccount.getId().toString());
            dashboard.setBalance(primaryAccount.getBalance());
        }
    }

    @Transactional
    public void createNewAccount(Integer userId) {
        accountService.createAccount(userId);
    }
}
