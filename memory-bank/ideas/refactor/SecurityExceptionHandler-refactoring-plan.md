# Plan Refaktoryzacji SecurityExceptionHandler

## 1. Analiza Obecnego Stanu

### Zidentyfikowane Problemy
1. **Duplikacja Kodu w Metodach Walidacji**
   - Dwie bardzo podobne metody handleValidationException
   - Powtarzająca się logika konwersji błędów
   - Podobna struktura tworzenia odpowiedzi

2. **Rozbudowana Metoda mapExceptionToError**
   - Długi switch pattern matching
   - Mieszanie różnych typów wyjątków
   - Brak jasnej kategoryzacji błędów

3. **Brak Hierarchii Wyjątków**
   - Płaska struktura wyjątków bezpieczeństwa
   - Brak wspólnej klasy bazowej
   - Utrudnione zarządzanie wspólnymi cechami

4. **Mieszanie Odpowiedzialności**
   - Logika walidacji w handlerze bezpieczeństwa
   - Bezpośrednie tworzenie obiektów odpowiedzi
   - Wbudowana logika logowania

5. **Nieoptymalne Zarządzanie Logowaniem**
   - Logika logowania zmieszana z obsługą wyjątków
   - Brak elastyczności w formatowaniu komunikatów
   - Ograniczone możliwości konfiguracji poziomów logowania

## 2. Szczegółowy Plan Implementacji

### Etap 1: Hierarchia Wyjątków Bezpieczeństwa
```java
public abstract class SecurityBaseException extends RuntimeException {
    private final ErrorCode errorCode;
    
    protected SecurityBaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

// Przykład implementacji konkretnego wyjątku
public class TokenNotFoundException extends SecurityBaseException {
    public TokenNotFoundException(String message) {
        super(message, ErrorCode.TOKEN_NOT_FOUND);
    }
}
```

### Etap 2: Wydzielenie Logiki Walidacji
1. **Utworzenie Interfejsu Konwertera**
```java
public interface ValidationErrorConverter<T> {
    ValidationError convert(T error);
}

// Implementacje dla różnych typów błędów
public class FieldErrorConverter implements ValidationErrorConverter<FieldError> {
    @Override
    public ValidationError convert(FieldError error) {
        return new ValidationError(
            error.getField(),
            error.getDefaultMessage(),
            String.valueOf(error.getRejectedValue())
        );
    }
}

public class ConstraintViolationConverter implements ValidationErrorConverter<ConstraintViolation<?>> {
    @Override
    public ValidationError convert(ConstraintViolation<?> violation) {
        return new ValidationError(
            violation.getPropertyPath().toString(),
            violation.getMessage(),
            violation.getInvalidValue() != null ? 
                violation.getInvalidValue().toString() : null
        );
    }
}
```

2. **Utworzenie ValidationExceptionHandler**
```java
@RestControllerAdvice
public class ValidationExceptionHandler {
    private final FieldErrorConverter fieldErrorConverter;
    private final ConstraintViolationConverter violationConverter;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationApiError> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        // Implementacja
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationApiError> handleValidationException(
            ConstraintViolationException ex,
            WebRequest request) {
        // Implementacja
    }
}
```

### Etap 3: Implementacja SecurityErrorMapper
```java
@Component
public class SecurityErrorMapper {
    private final Map<Class<? extends Exception>, ErrorCode> errorMappings;

    public SecurityErrorMapper() {
        this.errorMappings = initializeErrorMappings();
    }

    private Map<Class<? extends Exception>, ErrorCode> initializeErrorMappings() {
        Map<Class<? extends Exception>, ErrorCode> mappings = new HashMap<>();
        mappings.put(TokenNotFoundException.class, ErrorCode.TOKEN_NOT_FOUND);
        mappings.put(ExpiredTokenException.class, ErrorCode.TOKEN_EXPIRED);
        // ... więcej mapowań
        return Collections.unmodifiableMap(mappings);
    }

    public ErrorCode mapToErrorCode(Exception ex) {
        return errorMappings.getOrDefault(ex.getClass(), ErrorCode.INTERNAL_ERROR);
    }
}
```

### Etap 4: Implementacja ErrorLogger
```java
@Component
@Slf4j
public class ErrorLogger {
    public void logError(ErrorCode errorCode, Exception ex, String path) {
        String message = formatErrorMessage(errorCode, ex, path);
        
        if (isInternalError(errorCode)) {
            log.error(message, ex);
        } else {
            log.warn(message);
        }
    }

    private String formatErrorMessage(ErrorCode errorCode, Exception ex, String path) {
        return String.format(
            "Error occurred: %s, Path: %s, Message: %s",
            errorCode.name(),
            path,
            ex.getMessage()
        );
    }

    private boolean isInternalError(ErrorCode errorCode) {
        return errorCode == ErrorCode.INTERNAL_ERROR;
    }
}
```

### Etap 5: Refaktoryzacja SecurityExceptionHandler
```java
@RestControllerAdvice(basePackages = "info.mackiewicz.bankapp.security.controller")
public class SecurityExceptionHandler {
    private final SecurityErrorMapper errorMapper;
    private final ErrorLogger errorLogger;

    public SecurityExceptionHandler(
            SecurityErrorMapper errorMapper,
            ErrorLogger errorLogger) {
        this.errorMapper = errorMapper;
        this.errorLogger = errorLogger;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseApiError> handleException(
            Exception ex, 
            WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ErrorCode errorCode = errorMapper.mapToErrorCode(ex);
        
        errorLogger.logError(errorCode, ex, path);
        
        BaseApiError error = new BaseApiError(errorCode, path);
        return new ResponseEntity<>(error, error.getStatus());
    }
}
```

## 3. Kolejność Wdrażania Zmian

1. **Przygotowanie**
   - Utworzenie wszystkich nowych plików
   - Dodanie testów jednostkowych dla nowych komponentów

2. **Hierarchia Wyjątków**
   - Implementacja SecurityBaseException
   - Migracja istniejących wyjątków
   - Aktualizacja testów

3. **Logika Walidacji**
   - Wdrożenie konwerterów
   - Implementacja ValidationExceptionHandler
   - Testy dla nowej logiki walidacji

4. **Mapowanie i Logowanie**
   - Wdrożenie SecurityErrorMapper
   - Implementacja ErrorLogger
   - Testy dla nowych komponentów

5. **Finalizacja**
   - Refaktoryzacja SecurityExceptionHandler
   - Integracja wszystkich komponentów
   - Testy integracyjne
   - Aktualizacja dokumentacji

## 4. Korzyści z Refaktoryzacji

1. **Lepsza Organizacja Kodu**
   - Jasna hierarchia wyjątków
   - Oddzielenie logiki walidacji
   - Czytelniejsza struktura projektu

2. **Zwiększona Testowalność**
   - Mniejsze, wyspecjalizowane komponenty
   - Łatwiejsze mockowanie zależności
   - Lepsze pokrycie testami

3. **Łatwiejsze Utrzymanie**
   - Modułowa struktura
   - Jasne granice odpowiedzialności
   - Prostsze dodawanie nowych funkcjonalności

4. **Zgodność z Zasadami SOLID**
   - Single Responsibility Principle
   - Open/Closed Principle
   - Dependency Inversion Principle

5. **Ulepszone Zarządzanie Błędami**
   - Spójna obsługa wyjątków
   - Lepsze logowanie
   - Łatwiejsze debugowanie

## 5. Dalsze Możliwości Rozwoju

1. **Metryki i Monitoring**
   - Dodanie liczników błędów
   - Monitorowanie czasu odpowiedzi
   - Alerty dla krytycznych błędów

2. **Rozszerzenia Funkcjonalne**
   - Obsługa kolejnych typów wyjątków
   - Dodatkowe formaty odpowiedzi
   - Integracja z zewnętrznymi systemami monitoringu

3. **Optymalizacje Wydajności**
   - Buforowanie mapowań błędów
   - Asynchroniczne logowanie
   - Optymalizacja generowania odpowiedzi