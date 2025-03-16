package info.mackiewicz.bankapp.shared.error.translator;

import info.mackiewicz.bankapp.shared.error.ErrorCode;
import info.mackiewicz.bankapp.shared.error.ErrorContext;
import info.mackiewicz.bankapp.shared.error.ErrorDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseErrorTranslatorTest {

    private TestErrorTranslator translator;
    private ErrorContext context;

    // Test implementation of BaseErrorTranslator
    private static class TestErrorTranslator extends BaseErrorTranslator {
        private static final ErrorDomain TEST_DOMAIN = ErrorDomain.SECURITY; // Using SECURITY as test domain
        
        @Override
        public boolean supports(ErrorDomain domain) {
            return TEST_DOMAIN.equals(domain);
        }

        @Override
        protected String translateDomainSpecific(ErrorCode code, ErrorContext context) {
            if (code == ErrorCode.VALIDATION_ERROR) {
                return "Test domain validation error";
            }
            return null;
        }
    }

    @BeforeEach
    void setUp() {
        translator = new TestErrorTranslator();
        context = ErrorContext.builder()
                .path("/api/test")
                .domain(ErrorDomain.SECURITY)
                .build();
    }

    @Test
    void shouldHandleCommonErrors() {
        // Given context with common domain
        ErrorContext commonContext = ErrorContext.builder()
                .path("/api/common")
                .domain(ErrorDomain.COMMON)
                .build();

        // When translating common errors
        String validationMessage = translator.translate(ErrorCode.VALIDATION_ERROR, commonContext);
        String notFoundMessage = translator.translate(ErrorCode.RESOURCE_NOT_FOUND, commonContext);
        String tooManyAttemptsMessage = translator.translate(ErrorCode.TOO_MANY_ATTEMPTS, commonContext);

        // Then should return common error messages
        assertTrue(validationMessage.contains("invalid"));
        assertTrue(notFoundMessage.contains("could not be found"));
        assertTrue(tooManyAttemptsMessage.contains("Too many attempts"));
    }

    @Test
    void shouldPreferDomainSpecificTranslation() {
        // When translating an error that has both domain-specific and common translation
        String message = translator.translate(ErrorCode.VALIDATION_ERROR, context);

        // Then should use domain-specific translation
        assertEquals("Test domain validation error", message);
    }

    @Test
    void shouldFallbackToCommonTranslation() {
        // When translating an error that doesn't have domain-specific translation
        String message = translator.translate(ErrorCode.RESOURCE_NOT_FOUND, context);

        // Then should use common translation
        assertTrue(message.contains("could not be found"));
    }

    @Test
    void shouldUseDefaultMessageForUnhandledErrors() {
        // When translating an unhandled error
        String message = translator.translate(ErrorCode.INSUFFICIENT_FUNDS, context);

        // Then should return default message
        assertTrue(message.contains("unexpected error occurred"));
    }

    @Test
    void shouldHandleNullValues() {
        assertNotNull(translator.translate(null, context));
        assertNotNull(translator.translate(ErrorCode.VALIDATION_ERROR, null));
        assertNotNull(translator.translate(null, null));
    }
}