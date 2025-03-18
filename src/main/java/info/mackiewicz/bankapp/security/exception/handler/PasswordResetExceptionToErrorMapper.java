package info.mackiewicz.bankapp.security.exception.handler;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.security.exception.ExpiredTokenException;
import info.mackiewicz.bankapp.security.exception.PasswordChangeException;
import info.mackiewicz.bankapp.security.exception.TokenCreationException;
import info.mackiewicz.bankapp.security.exception.TokenException;
import info.mackiewicz.bankapp.security.exception.TokenNotFoundException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.security.exception.UnexpectedTokenValidationException;
import info.mackiewicz.bankapp.security.exception.UsedTokenException;
import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;

@Component
public class PasswordResetExceptionToErrorMapper implements ExceptionToErrorMapper {

    @Override
    public ErrorCode map(Exception ex) {    

        // Check if the exception is an instance of a known type
        return switch (ex) {
            // Security Exceptions
            case TokenNotFoundException e -> ErrorCode.TOKEN_NOT_FOUND;
            case ExpiredTokenException e -> ErrorCode.TOKEN_EXPIRED;
            case UsedTokenException e -> ErrorCode.TOKEN_USED;
            case TokenCreationException e -> ErrorCode.INTERNAL_ERROR;
            case UnexpectedTokenValidationException e -> ErrorCode.INTERNAL_ERROR;
            case TooManyPasswordResetAttemptsException e -> ErrorCode.TOO_MANY_PASSWORD_RESET_ATTEMPTS;
            case BadCredentialsException e -> ErrorCode.INVALID_CREDENTIALS;
            case TokenException e -> ErrorCode.TOKEN_INVALID;

            // User Exceptions
            case UserNotFoundException e -> ErrorCode.USER_NOT_FOUND;
            case PasswordChangeException e -> ErrorCode.INTERNAL_ERROR;

            // Fallback
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }
}
