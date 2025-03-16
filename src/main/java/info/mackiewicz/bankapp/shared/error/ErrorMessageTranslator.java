package info.mackiewicz.bankapp.shared.error;

/**
 * Interface for translating error codes into user-friendly messages.
 * Each domain (security, transaction, etc.) should have its own implementation.
 */
public interface ErrorMessageTranslator {
    /**
     * Checks if this translator supports the given domain
     *
     * @param domain The error domain to check
     * @return true if this translator handles the given domain
     */
    boolean supports(String domain);

    /**
     * Translates an error code into a user-friendly message
     *
     * @param code The error code to translate
     * @param context The context of the error
     * @return A user-friendly error message
     */
    String translate(ErrorCode code, ErrorContext context);
}