package info.mackiewicz.bankapp.system.error.handling.mapping;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.shared.exception.BankAppBaseException;
import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;

/**
 * Implements exception to error code mapping strategy for API exceptions.
 * Handles BankAppBaseException specifically and provides a default mapping for other exceptions.
 * 
 * @see BankAppBaseException
 * @see ErrorCode
 * @see ExceptionToErrorMapper
 */
@Component
public class ApiExceptionToErrorMapper implements ExceptionToErrorMapper {    

    /**
     * Maps exceptions to specific error codes. Extracts the error code from BankAppBaseException
     * instances or returns INTERNAL_ERROR for unhandled exception types.
     *
     * @param ex the exception to be mapped
     * @return ErrorCode extracted from BankAppBaseException or INTERNAL_ERROR for other exceptions
     * @see BankAppBaseException
     * @see ErrorCode#INTERNAL_ERROR
     */
    @Override
    public ErrorCode map(Exception ex) {    
        return switch (ex) {
            case BankAppBaseException e -> e.getErrorCode();

            default -> ErrorCode.INTERNAL_ERROR;
        };
    }
}
