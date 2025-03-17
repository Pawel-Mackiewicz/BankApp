# Plan implementacji rozszerzonej obsługi błędów walidacji

## 1. Nowe klasy pomocnicze

### ValidationError.java
```java
@Getter
@AllArgsConstructor
public class ValidationError {
    private String field;          // nazwa pola z błędem
    private String message;        // komunikat błędu
    private String rejectedValue;  // odrzucona wartość (opcjonalna)
}
```

## 2. Rozszerzenie ApiError

### ApiError.java - Zmiany
```java
public class ApiError {
    // istniejące pola...
    private List<ValidationError> errors;  // lista błędów walidacji

    // istniejący konstruktor...

    // nowy konstruktor dla błędów walidacji
    public ApiError(ErrorCode errorCode, String path, List<ValidationError> errors) {
        this(errorCode, path);
        this.errors = errors;
    }
}
```

## 3. Rozszerzenie SecurityExceptionHandler

### Dodanie metod obsługi walidacji:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiError> handleValidationException(
        MethodArgumentNotValidException ex, 
        WebRequest request) {
    
    String path = ((ServletWebRequest) request).getRequest().getRequestURI();
    
    List<ValidationError> validationErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(this::convertFieldError)
        .collect(Collectors.toList());

    ApiError apiError = new ApiError(
        ErrorCode.VALIDATION_ERROR, 
        path,
        validationErrors
    );

    logValidationErrors(path, validationErrors);
    return new ResponseEntity<>(apiError, apiError.getStatus());
}

@ExceptionHandler(ConstraintViolationException.class)
public ResponseEntity<ApiError> handleConstraintViolation(
        ConstraintViolationException ex, 
        WebRequest request) {
    
    String path = ((ServletWebRequest) request).getRequest().getRequestURI();
    
    List<ValidationError> validationErrors = ex.getConstraintViolations()
        .stream()
        .map(this::convertConstraintViolation)
        .collect(Collectors.toList());

    ApiError apiError = new ApiError(
        ErrorCode.VALIDATION_ERROR, 
        path,
        validationErrors
    );

    logValidationErrors(path, validationErrors);
    return new ResponseEntity<>(apiError, apiError.getStatus());
}

private ValidationError convertFieldError(FieldError fieldError) {
    return new ValidationError(
        fieldError.getField(),
        fieldError.getDefaultMessage(),
        fieldError.getRejectedValue() != null ? 
            fieldError.getRejectedValue().toString() : null
    );
}

private ValidationError convertConstraintViolation(ConstraintViolation<?> violation) {
    return new ValidationError(
        violation.getPropertyPath().toString(),
        violation.getMessage(),
        violation.getInvalidValue() != null ? 
            violation.getInvalidValue().toString() : null
    );
}

private void logValidationErrors(String path, List<ValidationError> errors) {
    log.debug("Validation failed for request to {}. Errors: {}", 
              path, 
              errors.stream()
                  .map(e -> String.format("%s: %s", e.getField(), e.getMessage()))
                  .collect(Collectors.joining(", "))
    );
}
```

## 4. Przykładowe odpowiedzi

### Dla MethodArgumentNotValidException
```json
{
    "status": "BAD_REQUEST",
    "message": "Validation failed",
    "path": "/api/auth/reset-password",
    "timestamp": "2025-03-17T18:52:47",
    "errors": [
        {
            "field": "password",
            "message": "Password must be at least 8 characters long",
            "rejectedValue": "123"
        },
        {
            "field": "token",
            "message": "Reset token is required",
            "rejectedValue": null
        }
    ]
}
```

### Dla ConstraintViolationException
```json
{
    "status": "BAD_REQUEST",
    "message": "Validation failed",
    "path": "/api/auth/login",
    "timestamp": "2025-03-17T18:52:47",
    "errors": [
        {
            "field": "email",
            "message": "must be a well-formed email address",
            "rejectedValue": "invalid.email"
        }
    ]
}
```

## 5. Korzyści implementacji

1. **Dla użytkownika końcowego:**
   - Dokładnie wie które pola są niepoprawne
   - Widzi wszystkie błędy na raz
   - Otrzymuje konkretne wskazówki co poprawić

2. **Dla frontendu:**
   - Może precyzyjnie oznaczać błędne pola
   - Może wyświetlać komunikaty przy konkretnych polach
   - Łatwe mapowanie błędów na formularz

3. **Dla developerów:**
   - Czytelne logi debugowe
   - Spójny format dla wszystkich błędów walidacji
   - Łatwe rozszerzanie o nowe typy błędów

## 6. Implementacja krok po kroku

1. Utworzenie klasy ValidationError
2. Rozszerzenie ApiError o obsługę listy błędów
3. Dodanie nowych handlerów w SecurityExceptionHandler
4. Dodanie metod pomocniczych do konwersji błędów
5. Implementacja logowania
6. Testy jednostkowe

## 7. Testy

```java
@Test
void whenPasswordTooShort_thenReturnsValidationErrors() {
    // given
    PasswordResetRequest request = new PasswordResetRequest("123", "token");
    
    // when
    ResponseEntity<ApiError> response = // wywołanie endpointu
    
    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody().getErrors())
        .hasSize(1)
        .anySatisfy(error -> {
            assertThat(error.getField()).isEqualTo("password");
            assertThat(error.getMessage()).contains("8 characters");
            assertThat(error.getRejectedValue()).isEqualTo("123");
        });
}
```

## 8. Następne kroki

1. Code review implementacji
2. Testy na środowisku developerskim
3. Dokumentacja zmian dla frontend developerów
4. Monitoring logów po wdrożeniu