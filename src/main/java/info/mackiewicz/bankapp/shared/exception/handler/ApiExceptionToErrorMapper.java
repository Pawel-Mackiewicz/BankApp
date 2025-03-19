package info.mackiewicz.bankapp.shared.exception.handler;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.exception.AccountValidationException;
import info.mackiewicz.bankapp.security.exception.PasswordResetBaseException;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;

@Component
public class ApiExceptionToErrorMapper implements ExceptionToErrorMapper {    

    @Override
    public ErrorCode map(Exception ex) {    
        return switch (ex) {
            case PasswordResetBaseException e -> ((PasswordResetBaseException) ex).getErrorCode();
            case AccountValidationException e -> ErrorCode.VALIDATION_ERROR;
            case UserNotFoundException e -> ErrorCode.USER_NOT_FOUND;
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }
}
