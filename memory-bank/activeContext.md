# Aktualny Kontekst Projektu

## Implementacja REST API dla Resetowania Hasła

### Specyfikacja API

### Struktura Klas

#### 1. DTO
```java
// PasswordResetRequestDTO.java
public class PasswordResetRequestDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}

// PasswordResetDTO.java
public class PasswordResetDTO {
    @NotBlank(message = "Token is required")
    private String token;
    
    @NotBlank(message = "New password is required")
    private String newPassword;
}
```

#### 2. Controller
```java
@RestController
@RequestMapping("/api/password")
@Validated
public class PasswordResetController {
    private final PasswordResetService passwordResetService;
    
    @PostMapping("/reset-request")
    public ResponseEntity<Void> requestReset(
        @Valid @RequestBody PasswordResetRequestDTO request
    ) {
        // Implementacja
    }
    
    @PostMapping("/reset-complete")
    public ResponseEntity<Void> completeReset(
        @Valid @RequestBody PasswordResetDTO request
    ) {
        // Implementacja
    }
}
```

#### 3. Service Interface
```java
public interface PasswordResetService {
    void requestReset(String email);
    void completeReset(String token, String newPassword);
}
```

#### 4. Obsługa Błędów

```java
@RestControllerAdvice
public class PasswordResetExceptionHandler {
    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorResponse handleRateLimit(RateLimitExceededException ex) {
        return new ErrorResponse("Too many reset attempts");
    }
    
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidToken(InvalidTokenException ex) {
        return new ErrorResponse("Invalid or expired token");
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        return new ErrorResponse("Validation failed");
    }
}
```

#### 5. Struktura Response

```java
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();
    
    public ErrorResponse(String message) {
        this.message = message;
    }
}
```

#### 6. REST API Endpoints

##### POST /api/password/reset-request
**Request Body**:
```json
{
  "email": "string"
}
```
**Response**:
- 200 OK: Email został wysłany
- 400 Bad Request: Nieprawidłowy format email
- 429 Too Many Requests: Przekroczono limit prób

#### 2. POST /api/password/reset-complete
**Request Body**:
```json
{
  "token": "string",
  "newPassword": "string"
}
```
**Response**:
- 200 OK: Hasło zostało zmienione
- 400 Bad Request: Nieprawidłowy format hasła
- 401 Unauthorized: Nieprawidłowy token
- 429 Too Many Requests: Przekroczono limit prób

### Wymagania Bezpieczeństwa
1. Rate Limiting:
   - 3 próby/godzinę per email
   - 5 prób/godzinę per IP
2. Walidacja hasła:
   - Minimum 8 znaków
   - Przynajmniej 1 cyfra
   - Przynajmniej 1 znak specjalny
   - Przynajmniej 1 wielka litera
3. Token:
   - JWT z czasem ważności 15 minut
   - Podpisany kluczem aplikacji
   - Zawiera email użytkownika

## Plan Implementacji Systemu Resetowania Hasła - 2 dni

### Dzień 1: Backend i Podstawowy Frontend

#### Rano (4h): Backend Core
1. PasswordResetController (2h):
   ```java
   @RestController
   @RequestMapping("/api/password")
   public class PasswordResetController {
       @PostMapping("/reset-request")    // Żądanie resetu
       @PostMapping("/reset-complete")   // Zmiana hasła
   }
   ```

2. Integracja Email (2h):
   - Szybka integracja z resend.com
   - Prosty szablon emaila
   - Podstawowy retry (max 3 próby)

#### Po południu (4h): Frontend Podstawowy
1. Formularze HTML (2h):
   - Link w login.html
   - reset-password.html
   - new-password.html

2. JavaScript Core (2h):
   ```javascript
   // reset-password.js
   async function requestReset(email)
   async function completeReset(token, password)
   ```

### Dzień 2: Security i Finalizacja

#### Rano (4h): Bezpieczeństwo
1. Rate Limiting (2h):
   ```java
   @Component
   public class SimpleRateLimiter {
       // Max 3 próby/godzinę per email
       // Max 5 prób/godzinę per IP
   }
   ```

2. Monitoring (2h):
   ```java
   @Service
   public class SecurityLogger {
       // Logowanie prób resetowania
       // Podstawowe alerty
   }
   ```

#### Po południu (4h): Testy i Finalizacja
1. Testy Krytyczne (2h):
   - Testy jednostkowe TokenService
   - Testy integracyjne flow resetowania
   - Testy bezpieczeństwa (rate limit)

2. Finalizacja i Deployment (2h):
   - Code review
   - Testy manualne
   - Deployment na staging

### Priorytety
1. Bezpieczeństwo:
   - Walidacja tokenów
   - Rate limiting
   - Podstawowe logowanie

2. UX:
   - Prosty, jasny interfejs
   - Komunikaty błędów
   - Walidacja formularzy

3. Monitoring:
   - Logowanie prób resetowania
   - Alerty o podejrzanych wzorcach
   - Metryki skuteczności

### Minimalna Funkcjonalność (MVP)
1. Backend:
   - Generowanie bezpiecznych tokenów
   - Wysyłka emaili
   - Podstawowa walidacja

2. Frontend:
   - Formularz "Zapomniałem hasła"
   - Formularz nowego hasła
   - Podstawowa walidacja JS

3. Bezpieczeństwo:
   - Prosty rate limiting
   - Logowanie zdarzeń
   - Podstawowe alerty

### Co Można Pominąć
1. Zaawansowane funkcje:
   - Złożone szablony email
   - Zaawansowane monitorowanie
   - Pełne pokrycie testami

2. Nice-to-have:
   - Customizacja wiadomości
   - Statystyki użycia
   - Zaawansowane metryki

### Kolejność Implementacji
1. Dzień 1 - Rano:
   09:00-11:00 - PasswordResetController
   11:00-13:00 - Integracja Email

2. Dzień 1 - Popołudnie:
   14:00-16:00 - Formularze HTML
   16:00-18:00 - Core JavaScript

3. Dzień 2 - Rano:
   09:00-11:00 - Rate Limiting
   11:00-13:00 - Security Logging

4. Dzień 2 - Popołudnie:
   14:00-16:00 - Testy Krytyczne
   16:00-18:00 - Deployment

### Definition of Done
1. Techniczne:
   - Działające endpointy API
   - Podstawowe testy przechodzą
   - Działa rate limiting

2. Biznesowe:
   - Użytkownik może zresetować hasło
   - System jest bezpieczny
   - Podstawowe monitorowanie działa