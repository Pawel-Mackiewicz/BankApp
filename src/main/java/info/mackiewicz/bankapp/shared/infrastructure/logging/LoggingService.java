package info.mackiewicz.bankapp.shared.infrastructure.logging;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for logging transaction-related operations in a consistent format.
 *
 * <p>This service provides specialized logging for:
 * <ul>
 *   <li>Account locking and unlocking during transactions</li>
 *   <li>Transaction attempts with detailed information</li>
 *   <li>Successful transaction completion with category-specific details</li>
 * </ul></p>
 *
 * <p>All logs use structured formats to ensure consistency and easy parsing.
 * Thread information is included where relevant for concurrent operation tracking.</p>
 *
 * <p>Thread-safe: This class is thread-safe as it uses SLF4J's thread-safe logging
 * and has no shared mutable state.</p>
 *
 * @see Account
 * @see Transaction
 * @see info.mackiewicz.bankapp.transaction.model.TransactionType
 */
@Slf4j
@Service
public class LoggingService {
    
    private String formatAccountInfo(Account account) {
        return account != null ? "ID:" + account.getId() : "N/A";
    }

    public void logLockingAccounts(Transaction transaction) {
        Account from = transaction.getSourceAccount();
        Account to = transaction.getDestinationAccount();
        String message = String.format("Transaction ID: %s, Locked accounts: %s, %s, Thread: %s",
                transaction.getId(), formatAccountInfo(from), formatAccountInfo(to), Thread.currentThread().getName());
        log.info(message);
    }

    public void logUnlockingAccounts(Transaction transaction) {
        Account from = transaction.getSourceAccount();
        Account to = transaction.getDestinationAccount();
        String message = String.format("Transaction ID: %s, Unlocked accounts: %s, %s, Thread: %s",
                transaction.getId(), formatAccountInfo(from), formatAccountInfo(to), Thread.currentThread().getName());
        log.info(message);
    }

    public void logTransactionAttempt(Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Attempt Transaction\n")
                .append("\tID: ").append(transaction.getId()).append("\n")
                .append("\tType: ").append(transaction.getType().getDisplayName()).append("\n")
                .append("\tAmount: ").append(transaction.getAmount()).append("\n");

        switch (transaction.getType().getCategory()) {
            case DEPOSIT -> sb.append("\tTo: ").append(formatAccountInfo(transaction.getDestinationAccount())).append("\n");
            case WITHDRAWAL, FEE -> sb.append("\tFrom: ").append(formatAccountInfo(transaction.getSourceAccount())).append("\n");
            case TRANSFER -> {
                sb.append("\tFrom: ").append(formatAccountInfo(transaction.getSourceAccount())).append("\n");
                sb.append("\tTo: ").append(formatAccountInfo(transaction.getDestinationAccount())).append("\n");
            }
        }
        log.info(sb.toString());
    }

    public void logSuccessfulTransaction(Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Successful Transaction\n\tID: ").append(transaction.getId());

        switch (transaction.getType().getCategory()) {
            case TRANSFER -> sb.append(String.format(
                    "\n\tAmount Sent: %.2f\n\tFrom: %s\n\tTo: %s",
                    transaction.getAmount(),
                    formatAccountInfo(transaction.getSourceAccount()),
                    formatAccountInfo(transaction.getDestinationAccount())));
            case DEPOSIT -> sb.append(String.format(
                    "\n\tDeposited: %.2f\n\tTo: %s",
                    transaction.getAmount(),
                    formatAccountInfo(transaction.getDestinationAccount())));
            case WITHDRAWAL -> sb.append(String.format(
                    "\n\tWithdrawn: %.2f\n\tFrom: %s",
                    transaction.getAmount(),
                    formatAccountInfo(transaction.getSourceAccount())));
            case FEE -> sb.append(String.format(
                    "\n\tFee: %.2f\n\tFrom: %s",
                    transaction.getAmount(),
                    formatAccountInfo(transaction.getSourceAccount())));
        }
        log.info(sb.toString());
    }
}
