package info.mackiewicz.bankapp.shared.error.translator;

import info.mackiewicz.bankapp.shared.error.ErrorCode;
import info.mackiewicz.bankapp.shared.error.ErrorContext;
import info.mackiewicz.bankapp.shared.error.ErrorMessageTranslator;

/**
 * Base translator that handles common errors and provides a template for domain-specific translations.
 */
public abstract class BaseErrorTranslator implements ErrorMessageTranslator {

    protected static final String DEFAULT_MESSAGE = 
        "An unexpected error occurred. Please try again or contact support if the problem persists.";

    @Override
    public final String translate(ErrorCode code, ErrorContext context) {
        if (code == null) {
            return DEFAULT_MESSAGE;
        }

        String message = null;

        // First try domain-specific translation if supported and context is present
        if (context != null && supports(context.getDomain())) {
            message = translateDomainSpecific(code, context);
        }

        return message == null ? translateCommonError(code, context) : message;
    }

    /**
     * Template method for domain-specific translations.
     * Return null if the error code is not handled by this translator.
     */
    protected abstract String translateDomainSpecific(ErrorCode code, ErrorContext context);

    /**
     * Handles common error translations.
     */
    protected String translateCommonError(ErrorCode code, ErrorContext context) {
        return switch (code) {
            case INTERNAL_ERROR -> "An unexpected error occurred. Please try again or contact support if the problem persists.";
            case VALIDATION_ERROR -> "The provided data is invalid. Please check your input and try again.";
            case RESOURCE_NOT_FOUND -> "The requested resource could not be found.";
            case TOO_MANY_ATTEMPTS -> "Too many attempts. Please try again later.";
            default -> DEFAULT_MESSAGE;
        };
    }
}