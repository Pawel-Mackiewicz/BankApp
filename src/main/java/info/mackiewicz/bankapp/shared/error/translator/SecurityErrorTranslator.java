package info.mackiewicz.bankapp.shared.error.translator;

import org.springframework.stereotype.Component;
import info.mackiewicz.bankapp.shared.error.ErrorCode;
import info.mackiewicz.bankapp.shared.error.ErrorContext;
import info.mackiewicz.bankapp.shared.error.ErrorDomain;

/**
 * Translator for security-related error messages.
 * Inherits common error handling from BaseErrorTranslator.
 */
@Component
public class SecurityErrorTranslator extends BaseErrorTranslator {
    
    @Override
    public boolean supports(ErrorDomain domain) {
        return ErrorDomain.SECURITY.equals(domain);
    }
    
    @Override
    protected String translateDomainSpecific(ErrorCode code, ErrorContext context) {
        // code cannot be null here, so we don't need to check for it
        
        return switch (code) {
            case TOKEN_EXPIRED -> "The password reset link has expired. Please request a new one.";
            case TOKEN_USED -> "This password reset link has already been used. Please request a new one if needed.";
            case TOKEN_NOT_FOUND -> "Invalid password reset link. Please make sure you're using the correct link or request a new one.";
            default -> null; // Let base class handle common errors or return default message
        };
    }
}