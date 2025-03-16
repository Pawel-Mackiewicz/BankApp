package info.mackiewicz.bankapp.shared.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ErrorCodeTest {

    @Test
    void shouldHaveCorrectHttpStatusForCommonErrors() {
        assertEquals(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND.getStatus());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.TOO_MANY_ATTEMPTS.getStatus());
    }

    @Test
    void shouldHaveCorrectHttpStatusForSecurityErrors() {
        assertEquals(HttpStatus.BAD_REQUEST, ErrorCode.TOKEN_EXPIRED.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, ErrorCode.TOKEN_USED.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, ErrorCode.TOKEN_NOT_FOUND.getStatus());
    }

    @Test
    void shouldHaveCorrectHttpStatusForTransactionErrors() {
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.INSUFFICIENT_FUNDS.getStatus());
        assertEquals(HttpStatus.FORBIDDEN, ErrorCode.ACCOUNT_LOCKED.getStatus());
    }

    @Test
    void shouldHaveCorrectDomainForCommonErrors() {
        assertEquals("common", ErrorCode.VALIDATION_ERROR.getDomain());
        assertEquals("common", ErrorCode.RESOURCE_NOT_FOUND.getDomain());
        assertEquals("common", ErrorCode.TOO_MANY_ATTEMPTS.getDomain());
    }

    @Test
    void shouldHaveCorrectDomainForSecurityErrors() {
        assertEquals("security", ErrorCode.TOKEN_EXPIRED.getDomain());
        assertEquals("security", ErrorCode.TOKEN_USED.getDomain());
        assertEquals("security", ErrorCode.TOKEN_NOT_FOUND.getDomain());
    }

    @Test
    void shouldHaveCorrectDomainForTransactionErrors() {
        assertEquals("transaction", ErrorCode.INSUFFICIENT_FUNDS.getDomain());
        assertEquals("transaction", ErrorCode.ACCOUNT_LOCKED.getDomain());
    }

    @Test
    void shouldHaveConsistentDomainGrouping() {
        Map<String, List<ErrorCode>> errorsByDomain = Arrays.stream(ErrorCode.values())
                .collect(Collectors.groupingBy(ErrorCode::getDomain));

        // Common domain should have exactly 3 errors
        assertEquals(3, errorsByDomain.get("common").size());
        
        // Security domain should have exactly 3 errors
        assertEquals(3, errorsByDomain.get("security").size());
        
        // Transaction domain should have exactly 2 errors
        assertEquals(2, errorsByDomain.get("transaction").size());
        
        // Should have exactly 3 domains
        assertEquals(3, errorsByDomain.size());
    }

    @Test
    void shouldNotContainNullValues() {
        for (ErrorCode code : ErrorCode.values()) {
            assertNotNull(code.getStatus(), "HTTP status should not be null for " + code);
            assertNotNull(code.getDomain(), "Domain should not be null for " + code);
        }
    }
}