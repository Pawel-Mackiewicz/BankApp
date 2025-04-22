package info.mackiewicz.bankapp.presentation.exception;

import info.mackiewicz.bankapp.system.error.handling.core.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

public abstract class PresentationBaseException extends BankAppBaseException{

    public PresentationBaseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public PresentationBaseException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

}
