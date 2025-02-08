package info.mackiewicz.bankapp.utils;

import info.mackiewicz.bankapp.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingService {
    private static final Logger LOCKS_LOGGER = LoggerFactory.getLogger("BankApp.locks");
    private static final Logger TRANSACTION_LOGGER = LoggerFactory.getLogger("BankApp.transaction");

    private static String formatAccountInfo(Account account) {
        return account != null ? "ID:" + account.getId() : "N/A";
    }

    public static void logErrorInMakingTransaction(Transaction transaction) {
        String message = String.format("Can't execute transaction ID: %s", transaction.getId());
        TRANSACTION_LOGGER.error(message);
    }

    public static void logErrorInMakingTransaction(Transaction transaction, String errorMessage) {
        String message = String.format("Error in Transaction ID: %s. %s", transaction.getId(), errorMessage);
        TRANSACTION_LOGGER.error(message);
    }

    public static void logLockingAccounts(Transaction transaction) {
        Account from = transaction.getSourceAccount();
        Account to = transaction.getDestinationAccount();
        String message = String.format("Transaction ID: %s, Locked accounts: %s, %s, Thread: %s",
                transaction.getId(), formatAccountInfo(from), formatAccountInfo(to), Thread.currentThread().getName());
        LOCKS_LOGGER.info(message);
    }

    public static void logUnlockingAccounts(Transaction transaction) {
        Account from = transaction.getSourceAccount();
        Account to = transaction.getDestinationAccount();
        String message = String.format("Transaction ID: %s, Unlocked accounts: %s, %s, Thread: %s",
                transaction.getId(), formatAccountInfo(from), formatAccountInfo(to), Thread.currentThread().getName());
        LOCKS_LOGGER.info(message);
    }

    public static void logTransactionAttempt(Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Attempt Transaction\n")
                .append("\tID: ").append(transaction.getId()).append("\n")
                .append("\tType: ").append(transaction.getType().getDisplayName()).append("\n")
                .append("\tAmount: ").append(transaction.getAmount()).append("\n");

        if (transaction.getType() != TransactionType.DEPOSIT) {
            sb.append("\tFrom: ").append(formatAccountInfo(transaction.getSourceAccount())).append("\n");
        }
        if (transaction.getType() != TransactionType.FEE && transaction.getType() != TransactionType.WITHDRAWAL) {
            sb.append("\tTo: ").append(formatAccountInfo(transaction.getDestinationAccount())).append("\n");
        }
        TRANSACTION_LOGGER.info(sb.toString());
    }

    public static void logSuccessfulTransaction(Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Successful Transaction\n\tID: ").append(transaction.getId());
        switch (transaction.getType()) {
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
        TRANSACTION_LOGGER.info(sb.toString());
    }

    public static void logFailedTransactionDueToInsufficientFunds(Transaction transaction) {
        String message = String.format("Transaction ID: %s, Insufficient Funds.", transaction.getId());
        TRANSACTION_LOGGER.warn(message);
    }
}
