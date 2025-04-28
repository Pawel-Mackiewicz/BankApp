package info.mackiewicz.bankapp.core.transaction.model;

/**
 * Represents the general category of transaction status.
 */
public enum TransactionStatusCategory {
    /**
     * Transaction is in progress or waiting to be processed
     */
    PROCESSING,

    /**
     * Transaction has been completed successfully
     */
    SUCCESS,

    /**
     * Transaction has failed
     */
    FAULTY
}