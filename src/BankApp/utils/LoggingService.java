package BankApp.utils;

import BankApp.model.Account;
import BankApp.model.Transaction;
import BankApp.model.TransactionType;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingService {
    private static final Logger locksLogger;
    private static final Logger transactionLogger;

    static {
        locksLogger = LoggerSetup.getLogger("locksLogger");
        transactionLogger = LoggerSetup.getLogger("transactionLogger");
    }

    public static synchronized void logErrorInMakingTransaction(Transaction transaction, String errorMessage) {
        String message = String.format("Error in Transaction ID: %s", errorMessage);
        locksLogger.log(Level.SEVERE, message);
    }

    public static synchronized void logLockingAccounts(Transaction currentTransaction) {
        Account to = currentTransaction.getTo();
        Account from = currentTransaction.getFrom();

        locksLogger.info(String.format("Transaction ID: %s, Locked accounts: %s %s",
                currentTransaction.getId(),
                from != null ? "\tID:" + from.getId() : "",
                to != null ? "\tID:" + to.getId() : ""));
    }

    public static synchronized void logUnlockingAccounts(Transaction currentTransaction) {
        Account from = currentTransaction.getFrom();
        Account to = currentTransaction.getTo();

        locksLogger.info(String.format("Transaction ID: %s, Unlocked accounts: %s %s",
                currentTransaction.getId(),
                from != null ? "\tID:" + from.getId() : "",
                to != null ? "\tID:" + to.getId() : ""));
    }

    public static synchronized void logTransactionAttempt(Transaction currentTransaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Attempt Transaction\n")
                .append("\tID: ").append(currentTransaction.getId()).append("\n")
                .append("\tType: ").append(currentTransaction.getType().getDisplayName()).append("\n")
                .append("\tAmount: ").append(currentTransaction.getAmount()).append("\n");

        if (currentTransaction.getType() != TransactionType.DEPOSIT) {
            sb.append("\tFrom: ").append(currentTransaction.getFrom()).append("\n");
        }
        if (currentTransaction.getType() != TransactionType.FEE && currentTransaction.getType() != TransactionType.WITHDRAWAL) {
            sb.append("\tTo: ").append(currentTransaction.getTo()).append("\n");
        }
        transactionLogger.info(sb.toString());
    }

    public static synchronized void logSuccessfulTransaction(Transaction currentTransaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Successful Transaction \n\tID: ").append(currentTransaction.getId()).append("\t");
        switch (currentTransaction.getType()) {
            case TRANSFER -> sb.append(String.format("\n\tAmount Sent: %d\t \n\tFrom: %s \n\tTo: %s\n",
                    currentTransaction.getAmount(), currentTransaction.getFrom(), currentTransaction.getTo()));
            case DEPOSIT -> sb.append(String.format("\n\tDeposited: %d\t \n\tTo: %s\n",
                    currentTransaction.getAmount(), currentTransaction.getTo()));
            case WITHDRAWAL -> sb.append(String.format("\n\tWithdrawn: %d\t \n\tFrom: %s\n",
                    currentTransaction.getAmount(), currentTransaction.getFrom()));
            case FEE -> sb.append(String.format("\n\tFee %d \n\tFrom: %s\n",
                    currentTransaction.getAmount(), currentTransaction.getFrom()));
        }
        transactionLogger.info(sb.toString());
    }

    public static synchronized void logFailedTransactionDueToInsufficientFunds(Transaction currentTransaction) {
        transactionLogger.warning(String.format("Transaction ID: %d can't be done. Not enough funds to make transaction.\n\n", currentTransaction.getId()));
    }
}
