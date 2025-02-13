package info.mackiewicz.bankapp.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import info.mackiewicz.bankapp.dto.DashboardDTO;
import info.mackiewicz.bankapp.dto.TransferForm;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionBuilder;
import info.mackiewicz.bankapp.model.TransactionType;

@Service
public class DashboardService {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;

    public DashboardService(AccountService accountService,
                          TransactionService transactionService,
                          TransactionBuilder transactionBuilder) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.transactionBuilder = transactionBuilder;
    }

    public DashboardDTO getDashboardData(Integer userId) {
        List<Account> accounts = getAccountsByUserId(userId);
        return buildDashboardDTO(accounts, userId);
    }

    private List<Account> getAccountsByUserId(Integer userId) {
        return accountService.getAccountsByOwnersId(userId);
    }

    private DashboardDTO buildDashboardDTO(List<Account> accounts, Integer userId) {
        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setAccounts(accounts);
        dashboard.setRecentTransactions(transactionService.getRecentTransactions(userId, 5));
        dashboard.setTotalBalance(calculateTotalBalance(accounts));
        
        setPrimaryAccountInfo(dashboard, accounts);
        return dashboard;
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
    public void processTransfer(TransferForm transferForm, Integer userId) {
        Account sourceAccount = accountService.getAccountById(transferForm.getSourceAccountId());
        validateTransfer(sourceAccount, transferForm, userId);
        executeTransfer(sourceAccount, transferForm);
    }

    private void validateTransfer(Account sourceAccount, TransferForm transferForm, Integer userId) {
        validateAccountOwnership(sourceAccount, userId);
        validateSufficientFunds(sourceAccount, transferForm.getAmount());
    }

    private void validateAccountOwnership(Account account, Integer userId) {
        if (!account.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("Not authorized to make transfers from this account");
        }
    }

    private void validateSufficientFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
    }

    private void executeTransfer(Account sourceAccount, TransferForm transferForm) {
        Transaction transfer = transactionBuilder
                .withSourceAccount(sourceAccount.getId())
                .withDestinationAccount(transferForm.getRecipientAccountId())
                .withAmount(transferForm.getAmount())
                .withType(TransactionType.TRANSFER)
                .withTransactionTitle(transferForm.getTitle())
                .build();

        transactionService.createTransaction(transfer);
    }

    @Transactional
    public void createNewAccount(Integer userId) {
        accountService.createAccount(userId);
    }
}
