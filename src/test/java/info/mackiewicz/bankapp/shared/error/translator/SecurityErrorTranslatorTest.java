package info.mackiewicz.bankapp.shared.error.translator;

import info.mackiewicz.bankapp.shared.error.ErrorCode;
import info.mackiewicz.bankapp.shared.error.ErrorContext;
import info.mackiewicz.bankapp.shared.error.ErrorDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityErrorTranslatorTest {

    private SecurityErrorTranslator translator;
    private ErrorContext context;

    @BeforeEach
    void setUp() {
        translator = new SecurityErrorTranslator();
        context = ErrorContext.builder()
                .path("/api/security/reset-password")
                .domain(ErrorDomain.SECURITY)
                .build();
    }

    @Test
    void shouldSupportSecurityDomain() {
        assertTrue(translator.supports(ErrorDomain.SECURITY));
        assertFalse(translator.supports(ErrorDomain.TRANSACTION));
        assertFalse(translator.supports(ErrorDomain.COMMON));
        assertFalse(translator.supports(null));
    }

    @Test
    void shouldTranslateTokenExpiredError() {
        String message = translator.translate(ErrorCode.TOKEN_EXPIRED, context);
        assertTrue(message.contains("expired"));
        assertTrue(message.contains("request a new one"));
    }

    @Test
    void shouldTranslateTokenUsedError() {
        String message = translator.translate(ErrorCode.TOKEN_USED, context);
        assertTrue(message.contains("already been used"));
        assertTrue(message.contains("request a new one"));
    }

    @Test
    void shouldTranslateTokenNotFoundError() {
        String message = translator.translate(ErrorCode.TOKEN_NOT_FOUND, context);
        assertTrue(message.contains("Invalid"));
        assertTrue(message.contains("correct link"));
    }

    @Test
    void shouldHandleCommonErrors() {
        // Should handle common errors through BaseErrorTranslator
        String validationMessage = translator.translate(ErrorCode.VALIDATION_ERROR, context);
        assertTrue(validationMessage.contains("invalid"));
        
        String notFoundMessage = translator.translate(ErrorCode.RESOURCE_NOT_FOUND, context);
        assertTrue(notFoundMessage.contains("could not be found"));
        
        String tooManyAttemptsMessage = translator.translate(ErrorCode.TOO_MANY_ATTEMPTS, context);
        assertTrue(tooManyAttemptsMessage.contains("Too many attempts"));
    }

    @Test
    void shouldHandleUnknownSecurityError() {
        // For any unhandled security domain error, should return default message
        ErrorCode mockSecurityError = ErrorCode.INSUFFICIENT_FUNDS; // Using transaction error as example
        String message = translator.translate(mockSecurityError, context);
        assertTrue(message.contains("unexpected error occurred"));
    }

    @Test
    void shouldHandleNullContext() {
        String message = translator.translate(ErrorCode.TOKEN_EXPIRED, null);
        assertNotNull(message);
        assertTrue(message.contains("expired"));
    }

    @Test
    void shouldHandleNullErrorCode() {
        String message = translator.translate(null, context);
        assertNotNull(message);
        assertTrue(message.contains("unexpected error occurred"));
    }
}