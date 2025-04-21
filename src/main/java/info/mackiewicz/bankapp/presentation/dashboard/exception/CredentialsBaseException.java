package info.mackiewicz.bankapp.presentation.dashboard.exception;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public abstract class CredentialsBaseException extends BankAppBaseException {

    public CredentialsBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    
    public CredentialsBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

}
