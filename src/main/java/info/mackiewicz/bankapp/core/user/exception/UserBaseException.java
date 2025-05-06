package info.mackiewicz.bankapp.core.user.exception;

import info.mackiewicz.bankapp.shared.exception.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.ErrorCode;

/**
 * Base exception class for user-related exceptions.
 * Extends BankAppBaseException to maintain consistent error handling.
 */
public abstract class UserBaseException extends BankAppBaseException {
    
    public UserBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UserBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}