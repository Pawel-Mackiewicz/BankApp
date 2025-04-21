package info.mackiewicz.bankapp.system.banking.history.exception;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public class UnsupportedExporterException extends TransactionHistoryBaseException {
    
    private static final ErrorCode ERROR_CODE = ErrorCode.UNSUPPORTED_EXPORTER;

    public UnsupportedExporterException(String message) {
        super(message, ERROR_CODE);
    }

    public UnsupportedExporterException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }

}
