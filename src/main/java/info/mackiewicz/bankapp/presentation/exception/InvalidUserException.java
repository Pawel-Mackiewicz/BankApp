package info.mackiewicz.bankapp.presentation.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class InvalidUserException extends BankAppBaseException {

    public InvalidUserException(String message) {
        super(message, ErrorCode.INVALID_CREDENTIALS);
    }
}
