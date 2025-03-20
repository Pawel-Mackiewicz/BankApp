package info.mackiewicz.bankapp.shared.web.error.mapping;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

/**
 * Defines a contract for mapping exceptions to specific error codes in the application.
 * This interface serves as a base for exception mapping strategies, allowing different
 * implementations to handle various types of exceptions differently.
 */
public interface ExceptionToErrorMapper {
    
    /**
     * Maps an exception to its corresponding error code.
     * 
     * @param ex the exception to be mapped
     * @return the corresponding ErrorCode for the given exception
     * @see info.mackiewicz.bankapp.shared.core.error.ErrorCode
     */
    ErrorCode map(Exception ex);
}