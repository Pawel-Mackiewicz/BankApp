# Aktualny Kontekst Projektu

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