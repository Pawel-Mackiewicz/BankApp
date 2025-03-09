package info.mackiewicz.bankapp.shared.util;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class LoggingService {

    private static String formatAccountInfo(Account account) {
        return account != null ? "ID:" + account.getId() : "N/A";
    }

    public static void logError(String message) {
        log.error(message);
    }

    
    public static void logError(String message, Throwable throwable) {
        log.error(message, throwable);
    }

    public static void logErrorInMakingTransaction(Transaction transaction) {
        String message = String.format("Can't execute transaction ID: %s", transaction.getId());
        log.error(message);
    }

    public static void logErrorInMakingTransaction(Transaction transaction, String errorMessage) {
        String message = String.format("Error in Transaction ID: %s. %s", transaction.getId(), errorMessage);
        log.error(message);
    }

    public static void logLockingAccounts(Transaction transaction) {
        Account from = transaction.getSourceAccount();
        Account to = transaction.getDestinationAccount();
        String message = String.format("Transaction ID: %s, Locked accounts: %s, %s, Thread: %s",
                transaction.getId(), formatAccountInfo(from), formatAccountInfo(to), Thread.currentThread().getName());
        log.info(message);
    }

    public static void logUnlockingAccounts(Transaction transaction) {
        Account from = transaction.getSourceAccount();
        Account to = transaction.getDestinationAccount();
        String message = String.format("Transaction ID: %s, Unlocked accounts: %s, %s, Thread: %s",
                transaction.getId(), formatAccountInfo(from), formatAccountInfo(to), Thread.currentThread().getName());
        log.info(message);
    }

    public static void logTransactionAttempt(Transaction transaction) {
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

    public static void logSuccessfulTransaction(Transaction transaction) {
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

    public static void logFailedTransactionDueToInsufficientFunds(Transaction transaction) {
        String message = String.format("Transaction ID: %s, Insufficient Funds.", transaction.getId());
        log.warn(message);
    }

    public static void logErrorInLockingAccounts(int accountId, Transaction transaction, String string) {
        String message = String.format("Error in locking accounts for transaction ID: %s, Account ID: %s, %s",
                transaction.getId(), accountId, string);
        log.error(message);
    }
    public static void logUnexpectedErrorInLockingAccounts(Transaction transaction, String string) {
        String message = String.format("Unexpected error in locking accounts for transaction ID: %s, %s",
                transaction.getId(), string);
        log.error(message);
    }
    
    public static void logErrorInUnlockingAccounts(int accountId, Transaction transaction, String string) {
        String message = String.format("Error in unlocking accounts for transaction ID: %s, Account ID: %s, %s",
                transaction.getId(), accountId, string);
        log.error(message);
    }

    public static void logUnexpectedErrorInUnlockingAccounts(Transaction transaction, String string) {
        String message = String.format("Unexpected error in unlocking accounts for transaction ID: %s, %s",
                transaction.getId(), string);
        log.error(message);
    }

}
