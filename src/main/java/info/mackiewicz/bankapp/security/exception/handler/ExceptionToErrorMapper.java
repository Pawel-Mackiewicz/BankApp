package info.mackiewicz.bankapp.security.exception.handler;

import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;

public interface ExceptionToErrorMapper {

    ErrorCode map(Exception ex);

}
