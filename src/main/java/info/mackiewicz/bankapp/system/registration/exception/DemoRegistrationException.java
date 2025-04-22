package info.mackiewicz.bankapp.system.registration.exception;

import info.mackiewicz.bankapp.system.error.handling.core.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public class DemoRegistrationException extends BankAppBaseException {

    private static final ErrorCode ERROR_CODE = ErrorCode.INTERNAL_ERROR;

    public DemoRegistrationException(String message) {
        super(message, ERROR_CODE);
    }
    public DemoRegistrationException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
}
