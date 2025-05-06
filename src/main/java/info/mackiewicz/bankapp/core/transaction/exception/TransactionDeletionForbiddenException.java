package info.mackiewicz.bankapp.core.transaction.exception;

import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

/**
 * Exception thrown when an attempt is made to delete a transaction that is not allowed to be deleted.
 * <p>
 * This exception is typically used in scenarios where specific business rules or transaction states
 * forbid the deletion of certain transactions, such as transactions that are already processed
 * or currently in process.
 * <p>
 * The associated error code for this exception is {@code ErrorCode.TRANSACTION_NOT_DELETABLE}.
 */
public class TransactionDeletionForbiddenException extends TransactionBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.TRANSACTION_NOT_DELETABLE;

    public TransactionDeletionForbiddenException(String message) {
        super(message, ERROR_CODE);
    }

    public TransactionDeletionForbiddenException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
