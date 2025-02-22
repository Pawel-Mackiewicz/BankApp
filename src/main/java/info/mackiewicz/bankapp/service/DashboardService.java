package info.mackiewicz.bankapp.service;

import java.math.BigDecimal;
import java.util.Comparator;
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
    public void processTransfer(TransferForm transferForm, Integer userId) {
        validateTransfer(transferForm, userId);
        executeTransfer(transferForm);
    }

    private void validateTransfer(TransferForm transferForm, Integer userId) {
        Account sourceAccount = accountService.getAccountById(transferForm.getSourceAccountId());
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

    private void executeTransfer(TransferForm transferForm) {
        Transaction transfer = transactionBuilder
                .withSourceAccount(transferForm.getSourceAccountId())
                .withDestinationAccount(transferForm.getRecipientAccountId())
                .withAmount(transferForm.getAmount())
                .withType(getTransactionType(transferForm))
                .withTransactionTitle(transferForm.getTitle())
                .build();

        transactionService.createTransaction(transfer);

    }

    private TransactionType getTransactionType(TransferForm transferForm) {
        Account acc1 = accountService.getAccountById(transferForm.getSourceAccountId());
        Account acc2 = accountService.getAccountById(transferForm.getRecipientAccountId());
        return acc1.getOwnerId().equals(acc2.getOwnerId())
                ? TransactionType.TRANSFER_OWN
                : TransactionType.TRANSFER_INTERNAL;
    }
    @Transactional
    public void createNewAccount(Integer userId) {
        accountService.createAccount(userId);
    }
}
