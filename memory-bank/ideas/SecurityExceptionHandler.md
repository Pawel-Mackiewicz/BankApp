# Security Exception Handler - Detailed Implementation Plan

## Phase 1: Core Error System

### 1. Create Common Error Code System
```java
package info.mackiewicz.bankapp.shared.error;

public enum ErrorCode {
    // Common validation
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS),
    
    // Security domain
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST),
    TOKEN_USED(HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND),
    
    // Transaction domain
    INSUFFICIENT_FUNDS(HttpStatus.UNPROCESSABLE_ENTITY),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN);

    private final HttpStatus status;
    private final String domain;  // For grouping related errors
    
    ErrorCode(HttpStatus status) {
        this.status = status;
        this.domain = this.name().split("_")[0].toLowerCase();
    }
}
```

### 2. Create Error Translation System

a) Create Error Context:
```java
package info.mackiewicz.bankapp.shared.error;

public class ErrorContext {
    private final String path;     // Request path
    private final String domain;   // Module domain (security, transaction, etc.)
    private final Map<String, Object> attributes;  // Additional context
    
    // Builder pattern implementation
}
```

b) Create Translator Interface:
```java
package info.mackiewicz.bankapp.shared.error;

public interface ErrorMessageTranslator {
    boolean supports(String domain);
    String translate(ErrorCode code, ErrorContext context);
}
```

c) Implement Security Translator:
```java
@Component
public class SecurityErrorTranslator implements ErrorMessageTranslator {
    @Override
    public boolean supports(String domain) {
        return "security".equals(domain);
    }
    
    @Override
    public String translate(ErrorCode code, ErrorContext context) {
        return switch (code) {
            case TOKEN_EXPIRED -> "Password reset link has expired...";
            case TOKEN_USED -> "This reset link has already been used...";
            case TOO_MANY_ATTEMPTS -> "Too many attempts...";
            default -> "An error occurred...";
        };
    }
}
```

## Phase 2: API Layer Updates

### 3. Update ApiExceptionHandler

a) Update Class Annotations:
```java
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApiExceptionHandler {
    private final ErrorCodeResolver errorCodeResolver;
}
```

b) Create Error Code Resolver:
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
        var message = translator.translate(ErrorCode.TOKEN_EXPIRED, new ErrorContext());
        assertEquals("Password reset link has expired...", message);
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
