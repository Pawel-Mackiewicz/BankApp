package info.mackiewicz.bankapp.core.user.exception;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

/**
 * Exception thrown when a requested user cannot be found.
 */
public class UserNotFoundException extends UserBaseException {
    private static final String DEFAULT_MESSAGE = "We couldn't find user with the provided information.";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE, ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(String message) {
        super(message, ErrorCode.USER_NOT_FOUND);
    }
}
