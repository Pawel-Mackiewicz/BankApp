# Implementacja obsługi błędów z użyciem interfejsów

## 1. Hierarchia interfejsów i klas

### ApiErrorResponse.java (interfejs bazowy)
```java
public interface ApiErrorResponse {
    HttpStatus getStatus();
    String getTitle();
    String getMessage();
    String getPath();
    LocalDateTime getTimestamp();
}
```

### ValidationErrorResponse.java (interfejs dla błędów walidacji)
```java
public interface ValidationErrorResponse extends ApiErrorResponse {
    List<ValidationError> getErrors();
}
```

### BaseApiError.java (podstawowa implementacja)
```java
@Getter
public class BaseApiError implements ApiErrorResponse {
    private final HttpStatus status;
    private final String title;
    private final String message;
    private final String path;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private final LocalDateTime timestamp;

    public BaseApiError(ErrorCode errorCode, String path) {
        this.status = errorCode.getStatus();
        this.title = errorCode.name();
        this.message = errorCode.getMessage();
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
```

### ValidationApiError.java (implementacja dla błędów walidacji)
```java
@Getter
public class ValidationApiError extends BaseApiError implements ValidationErrorResponse {
    private final List<ValidationError> errors;

    public ValidationApiError(ErrorCode errorCode, String path, List<ValidationError> errors) {
        super(errorCode, path);
        this.errors = errors;
    }
}
```

## 2. Modyfikacja SecurityExceptionHandler

```java
@RestControllerAdvice(basePackages = "info.mackiewicz.bankapp.security.controller")
public class SecurityExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, 
            WebRequest request) {
        
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        
        List<ValidationError> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::convertFieldError)
            .collect(Collectors.toList());

        ValidationErrorResponse response = new ValidationApiError(
            ErrorCode.VALIDATION_ERROR, 
            path,
            validationErrors
        );

        logValidationErrors(path, validationErrors);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        
        ErrorCode errorCode = mapExceptionToError(ex);
        ApiErrorResponse response = new BaseApiError(errorCode, path);
        logError(errorCode, ex, path);
        
        return new ResponseEntity<>(response, response.getStatus());
    }

    // ... reszta implementacji pozostaje bez zmian
}
```

## 3. Przykładowe odpowiedzi

### Standardowy błąd
```json
{
    "status": "INTERNAL_SERVER_ERROR",
    "title": "INTERNAL_ERROR",
    "message": "Internal server error occurred",
    "path": "/api/auth/login",
    "timestamp": "17-03-2025 19:00:43"
}
```

### Błąd walidacji
```json
{
    "status": "BAD_REQUEST",
    "title": "VALIDATION_ERROR",
    "message": "Validation failed",
    "path": "/api/auth/reset-password",
    "timestamp": "17-03-2025 19:00:43",
    "errors": [
        {
            "field": "password",
            "message": "Password must be at least 8 characters long",
            "rejectedValue": "123"
        }
    ]
}
```

## 4. Korzyści tego podejścia

1. **Separacja odpowiedzialności**:
   - Każdy typ błędu ma własną implementację
   - Lista błędów walidacji jest tylko tam, gdzie jest potrzebna
   - Czysta hierarchia interfejsów

2. **Elastyczność**:
   - Łatwe dodawanie nowych typów odpowiedzi błędów
   - Możliwość rozszerzania interfejsów
   - Łatwe dodawanie nowych pól dla konkretnych typów błędów

3. **Typebezpieczeństwo**:
   - Kompilator pomoże wykryć błędy
   - Jasne kontrakty interfejsów
   - Bezpieczne rzutowanie typów

4. **Mniejszy narzut pamięciowy**:
   - Pole errors istnieje tylko w ValidationApiError
   - Standardowe błędy nie mają niepotrzebnych pól
   - Efektywniejsza serializacja JSON

## 5. Testy

```java
@Test
void whenStandardError_thenNoValidationFields() {
    // given
    Exception ex = new RuntimeException("Test");
    
    // when
    ResponseEntity<ApiErrorResponse> response = handler.handleException(ex, webRequest);
    
    // then
    assertThat(response.getBody())
        .isInstanceOf(BaseApiError.class)
        .isNotInstanceOf(ValidationErrorResponse.class);
}

@Test
void whenValidationError_thenIncludesErrorsList() {
    // given
    MethodArgumentNotValidException ex = // ... setup validation exception
    
    // when
    ResponseEntity<ValidationErrorResponse> response = 
        handler.handleValidationException(ex, webRequest);
    
    // then
    assertThat(response.getBody())
        .isInstanceOf(ValidationErrorResponse.class);
    assertThat(response.getBody().getErrors())
        .isNotEmpty();
}
```

## 6. Kolejne kroki

1. Implementacja interfejsów i klas bazowych
2. Aktualizacja handlera błędów
3. Testy jednostkowe
4. Testy integracyjne z frontendem
5. Dokumentacja API dla frontendowców