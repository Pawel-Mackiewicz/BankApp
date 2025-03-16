# Security Exception Handler - Detailed Implementation Plan

## Phase 1: Core Error System

### 1. Create Error Domain and Code Systems

a) Create Error Domain Enum:
```java
package info.mackiewicz.bankapp.shared.error;

public enum ErrorDomain {
    COMMON("common"),
    SECURITY("security"),
    TRANSACTION("transaction");
    
    private final String value;
    
    ErrorDomain(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static ErrorDomain fromString(String value) {
        if (value == null) return null;
        
        for (ErrorDomain domain : ErrorDomain.values()) {
            if (domain.value.equals(value)) {
                return domain;
            }
        }
        return null;
    }
}
```

b) Create Error Code Enum:
```java
package info.mackiewicz.bankapp.shared.error;

public enum ErrorCode {
    // Common validation
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, ErrorDomain.COMMON),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorDomain.COMMON),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, ErrorDomain.COMMON),
    
    // Security domain
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, ErrorDomain.SECURITY),
    TOKEN_USED(HttpStatus.BAD_REQUEST, ErrorDomain.SECURITY),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorDomain.SECURITY),
    
    // Transaction domain
    INSUFFICIENT_FUNDS(HttpStatus.UNPROCESSABLE_ENTITY, ErrorDomain.TRANSACTION),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, ErrorDomain.TRANSACTION);

    private final HttpStatus status;
    private final ErrorDomain domain;
    
    ErrorCode(HttpStatus status, ErrorDomain domain) {
        this.status = status;
        this.domain = domain;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public ErrorDomain getDomain() {
        return domain;
    }
    
    /**
     * Get string representation of the domain.
     * @return domain value as string
     */
    public String getDomainValue() {
        return domain.getValue();
    }
}
```

### 2. Create Error Translation System

a) Create Error Context:
```java
package info.mackiewicz.bankapp.shared.error;

@Builder
public class ErrorContext {
    private final String path;     // Request path
    private final ErrorDomain domain;   // Module domain (security, transaction, etc.)
    private final Map<String, Object> attributes;  // Additional context
}
```

b) Create Translator Interface:
```java
package info.mackiewicz.bankapp.shared.error;

public interface ErrorMessageTranslator {
    boolean supports(ErrorDomain domain);
    String translate(ErrorCode code, ErrorContext context);
}
```

c) Create Base Error Translator:
```java
public abstract class BaseErrorTranslator implements ErrorMessageTranslator {
    protected static final String DEFAULT_MESSAGE =
        "An unexpected error occurred. Please try again or contact support if the problem persists.";

    @Override
    public final String translate(ErrorCode code, ErrorContext context) {
        if (code == null) {
            return DEFAULT_MESSAGE;
        }

        String message = null;

        // First try domain-specific translation if supported
        if (context != null && supports(context.getDomain())) {
            message = translateDomainSpecific(code, context);
        }

        return message == null ? translateCommonError(code, context) : message;
    }

    protected abstract String translateDomainSpecific(ErrorCode code, ErrorContext context);

    protected String translateCommonError(ErrorCode code, ErrorContext context) {
        return switch (code) {
            case VALIDATION_ERROR -> "The provided data is invalid. Please check your input and try again.";
            case RESOURCE_NOT_FOUND -> "The requested resource could not be found.";
            case TOO_MANY_ATTEMPTS -> "Too many attempts. Please try again later.";
            default -> DEFAULT_MESSAGE;
        };
    }
}
```

d) Implement Security Translator:
```java
@Component
public class SecurityErrorTranslator extends BaseErrorTranslator {
    @Override
    public boolean supports(ErrorDomain domain) {
        return ErrorDomain.SECURITY.equals(domain);
    }
    
    @Override
    protected String translateDomainSpecific(ErrorCode code, ErrorContext context) {
        return switch (code) {
            case TOKEN_EXPIRED -> "The password reset link has expired. Please request a new one.";
            case TOKEN_USED -> "This password reset link has already been used. Please request a new one if needed.";
            case TOKEN_NOT_FOUND -> "Invalid password reset link. Please make sure you're using the correct link or request a new one.";
            default -> null; // Let base class handle common errors or return default message
        };
    }
}
```

## Phase 2: API Layer Updates

### 3. Update ApiExceptionHandler

a) Create Error Code Resolver:
```java
@Component
public class ErrorCodeResolver {
    public ErrorCode resolveErrorCode(Exception ex) {
        return switch (ex) {
            case TokenNotFoundException e -> ErrorCode.TOKEN_NOT_FOUND;
            case ExpiredPasswordResetTokenException e -> ErrorCode.TOKEN_EXPIRED;
            case UsedPasswordResetTokenException e -> ErrorCode.TOKEN_USED;
            case TooManyPasswordResetAttemptsException e -> ErrorCode.TOO_MANY_ATTEMPTS;
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }
}
```

b) Update Class Annotations:
```java
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApiExceptionHandler {
    private final ErrorCodeResolver errorCodeResolver;
}
```

c) Update Exception Handlers:
```java
@ExceptionHandler(TokenNotFoundException.class)
public ResponseEntity<ApiResponse<?>> handleTokenNotFound(TokenNotFoundException ex) {
    ErrorCode errorCode = errorCodeResolver.resolveErrorCode(ex);
    log.debug("Token not found: {}", ex.getMessage());
    
    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ApiResponse.error(errorCode.name(), errorCode.getStatus()));
}

// Similar handlers for other exceptions...
```

## Phase 3: Web Layer Updates

### 4. Update WebExceptionHandler

a) Update Dependencies:
```java
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class WebExceptionHandler {
    private final List<ErrorMessageTranslator> translators;
    private final ObjectMapper objectMapper;
}
```

b) Create Error Response Parser:
```java
@Component
public class ApiErrorResponseParser {
    private final ObjectMapper objectMapper;
    
    public Optional<ErrorCode> extractErrorCode(HttpStatusCodeException ex) {
        try {
            ApiResponse<?> response = objectMapper.readValue(
                ex.getResponseBodyAsString(), 
                new TypeReference<ApiResponse<?>>() {}
            );
            return Optional.ofNullable(ErrorCode.valueOf(response.getMessage()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
```

c) Implement Translation Logic:
```java
@Component
public class ErrorTranslationService {
    private final List<ErrorMessageTranslator> translators;
    
    public String translateError(ErrorCode code, ErrorContext context) {
        return translators.stream()
            .filter(t -> t.supports(context.getDomain()))
            .findFirst()
            .map(t -> t.translate(code, context))
            .orElse("An unexpected error occurred");
    }
}
```

d) Update HttpStatusCodeException Handler:
```java
@ExceptionHandler(HttpStatusCodeException.class)
public ModelAndView handleHttpStatusCodeException(
        HttpStatusCodeException ex,
        HttpServletRequest request
) {
    ErrorContext context = ErrorContext.builder()
        .path(request.getRequestURI())
        .domain(extractDomainFromPath(request.getRequestURI()))
        .build();
        
    ErrorCode errorCode = errorResponseParser
        .extractErrorCode(ex)
        .orElse(ErrorCode.INTERNAL_ERROR);
        
    String userMessage = translationService.translateError(errorCode, context);
    
    return createErrorModelAndView(
        "Error",
        userMessage,
        ex.getStatusCode()
    );
}
```

## Phase 4: Configuration Updates

### 5. Create Configuration Class
```java
@Configuration
public class ErrorHandlingConfig {
    @Bean
    public List<ErrorMessageTranslator> errorMessageTranslators() {
        return List.of(
            new SecurityErrorTranslator(),
            new TransactionErrorTranslator(),
            new DefaultErrorTranslator()
        );
    }
}
```

## Phase 5: Testing

### 6. Unit Tests

a) Error Code Tests:
```java
class ErrorCodeTest {
    @Test
    void shouldHaveCorrectHttpStatus() {
        assertEquals(HttpStatus.BAD_REQUEST, ErrorCode.TOKEN_EXPIRED.getStatus());
    }
}
```

b) Translator Tests:
```java
class SecurityErrorTranslatorTest {
    @Test
    void shouldTranslateTokenExpired() {
        var translator = new SecurityErrorTranslator();
        var context = ErrorContext.builder()
            .path("/reset-password")
            .domain(ErrorDomain.SECURITY)
            .build();
        var message = translator.translate(ErrorCode.TOKEN_EXPIRED, context);
        assertEquals("The password reset link has expired. Please request a new one.", message);
    }
}
```

### 7. Integration Tests:
```java
@WebMvcTest
class ErrorHandlingIntegrationTest {
    @Test
    void whenTokenExpired_shouldShowUserFriendlyMessage() {
        mockMvc.perform(post("/password-reset"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("error", 
                containsString("Password reset link has expired")));
    }
}
```

## Implementation Order

1. Create base classes:
   - ErrorCode enum
   - ErrorContext
   - ErrorMessageTranslator interface

2. Implement API layer:
   - ErrorCodeResolver
   - Update ApiExceptionHandler
   - Add error code mapping

3. Implement translation system:
   - SecurityErrorTranslator
   - ApiErrorResponseParser
   - ErrorTranslationService

4. Update web layer:
   - Update WebExceptionHandler
   - Add context building
   - Integrate translation

5. Add tests:
   - Unit tests for each component
   - Integration tests for full flow
