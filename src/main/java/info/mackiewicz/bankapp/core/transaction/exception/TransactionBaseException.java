package info.mackiewicz.bankapp.core.transaction.exception;

import info.mackiewicz.bankapp.shared.exception.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

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