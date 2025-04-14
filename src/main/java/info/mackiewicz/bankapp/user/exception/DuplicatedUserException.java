package info.mackiewicz.bankapp.user.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Exception thrown when attempting to create a user that already exists.
 * Uses INTERNAL_ERROR to avoid exposing sensitive information about which field caused the duplication.
 */
public class DuplicatedUserException extends UserBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.USER_ALREADY_EXISTS;

    public DuplicatedUserException(String message) {
        super(message, ERROR_CODE);
    }

    public DuplicatedUserException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
