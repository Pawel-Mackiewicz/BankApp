package info.mackiewicz.bankapp.transaction.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Base exception class for transaction-related exceptions in the BankApp application.
 * This class extends BankAppBaseException to provide unified error handling for transaction operations.
 */
public abstract class TransactionBaseException extends BankAppBaseException {
    public TransactionBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public TransactionBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}