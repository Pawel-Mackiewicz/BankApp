## New Session Context
03.01.2025, 17:43

## Recent Architectural Decisions
- Designed IP-based rate limiting system
  * Utilizing Spring Security's built-in features
  * In-memory solution with Guava Cache
  * Login attempts: 10 per hour per IP
  * Password reset attempts: 5 per hour per IP
  * Automatic cleanup after 1 hour
  * Integration with Spring Security events

## Recent Changes

## Recent Changes
- Password reset system fully implemented and tested
- New HTML/CSS email template system deployed
- Added content personalization variables
- Updated EmailService for new templates
- Extended email content testing
- Added app.base-url configuration

## Current Goals
1. Production testing of template system:
   - Verify display in various email clients
   - Test responsiveness
   - Performance testing of sending system

2. Documentation preparation:
   - Template creation guide
   - Available personalization variables list
   - Template style guide

## Open Questions
1. Should we add multi-language support for templates?
2. Is it worth implementing email open tracking?
3. Should we allow users to customize template colors?
4. What additional email types might be needed in the future?

---

## Previous Session Context
28.02.2025, 15:30

## Recent Changes
- Zaimplementowano nowy system szablonów emaili z użyciem HTML i CSS
- Dodano system zmiennych do personalizacji treści
- Zaktualizowano EmailService do pracy z nowymi szablonami
- Rozszerzono testy o sprawdzanie zawartości emaili
- Dodano konfigurację app.base-url
- Zaktualizowano dokumentację w decisionLog.md i progress.md

## Current Goals
1. Przetestowanie systemu szablonów w środowisku produkcyjnym:
   - Sprawdzenie wyświetlania w różnych klientach pocztowych
   - Weryfikacja responsywności
   - Testy wydajności wysyłania

2. Przygotowanie dokumentacji:
   - Instrukcja tworzenia nowych szablonów
   - Lista dostępnych zmiennych do personalizacji
   - Przewodnik stylów dla szablonów

## Open Questions
1. Czy należy dodać w przyszłości obsługę wielu języków w szablonach?
2. Czy warto zaimplementować system śledzenia otwarć emaili?
3. Czy powinniśmy umożliwić użytkownikom personalizację kolorystyki?
4. Jakie dodatkowe typy emaili mogą być potrzebne w przyszłości?

# Aktualny Kontekst Projektu

## System Resetowania Hasła - Zakończony

### Zaimplementowane Komponenty
1. Token Generation & Storage:
   - Secure random token generation (32 bytes)
   - SHA-256 hashing implementation
   - Hash storage in database
   - Proper token validation

2. Database Implementation:
   - token_hash column for storing hashed tokens
   - User email association
   - Expiration tracking
   - Usage tracking

3. Implementation Status:
   - Implementation complete
   - Testing completed successfully
   - System działa poprawnie

4. Zaimplementowane Funkcjonalności:
   - Generowanie bezpiecznych tokenów
   - Wysyłanie emaili z linkiem resetującym
   - Walidacja tokenów
   - Zmiana hasła
   - Rate limiting
   - Monitoring bezpieczeństwa

## Password Reset System Implementation

### Current Implementation Status
1. Core Components Implemented:
   - PasswordResetToken model
   - PasswordResetTokenRepository
   - TokenHashingService (using SHA-256)
   - PasswordResetTokenService
   - PasswordResetController
   - Frontend forms and validation

2. Security Features:
   - Token rate limiting (max 2 active tokens per user)
   - Token expiration (60 minutes)
   - Token usage tracking
   - Database cleanup for old tokens
   - Email integration with resend.com

### Specyfikacja API

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

## Zakończona Implementacja Systemu Resetowania Hasła

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

### Osiągnięte Cele
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

### Zaimplementowana Funkcjonalność (MVP)
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