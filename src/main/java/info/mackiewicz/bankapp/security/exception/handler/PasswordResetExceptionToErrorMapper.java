package info.mackiewicz.bankapp.security.exception.handler;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.security.exception.PasswordResetException;
import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;

@Component
public class PasswordResetExceptionToErrorMapper implements ExceptionToErrorMapper {    

    @Override
    public ErrorCode map(Exception ex) {    
        return switch (ex) {
            case PasswordResetException e -> ((PasswordResetException) ex).getErrorCode();
            case UserNotFoundException e -> ErrorCode.USER_NOT_FOUND;
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }
}
