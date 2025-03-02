# Architectural Decisions Log

## [02.03.2025] - Complete Domain Structure

### Context
Need to map all existing files to appropriate domains, including both core business domains and the presentation layer.

### Structure

```
src/main/java/info/mackiewicz/bankapp/
├── user/                  # User Domain
│   ├── model/
│   │   ├── User.java
│   │   ├── AdminUser.java
│   │   └── dto/
│   │       ├── UpdateUserRequest.java
│   │       └── UserRegistrationDto.java
│   ├── service/
│   │   ├── UserService.java
│   │   ├── UserServiceInterface.java
│   │   ├── AdminUserService.java
│   │   ├── UserRegistrationService.java
│   │   └── UsernameGeneratorService.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   └── AdminUserRepository.java
│   └── validation/
│       └── Adult.java
│       └── AdultValidator.java

├── account/              # Account Domain
│   ├── model/
│   │   ├── Account.java
│   │   └── dto/
│   │       ├── AccountOwnerDTO.java
│   │       └── CreateAccountRequest.java
│   ├── service/
│   │   ├── AccountService.java
│   │   └── AccountServiceInterface.java
│   ├── repository/
│   │   └── AccountRepository.java
│   └── validation/
│       └── IbanValidator.java

├── transaction/          # Transaction Domain
│   ├── model/
│   │   ├── Transaction.java
│   │   ├── TransactionType.java
│   │   ├── TransactionStatus.java
│   │   ├── TransactionCategory.java
│   │   └── dto/
│   │       ├── CreateTransactionRequest.java
│   │       ├── TransferRequest.java
│   │       └── TransactionFilterDTO.java
│   ├── service/
│   │   ├── TransactionService.java
│   │   ├── TransactionProcessor.java
│   │   ├── TransactionFilterService.java
│   │   └── strategy/
│   │       ├── TransactionStrategy.java
│   │       ├── TransferTransaction.java
│   │       ├── DepositTransaction.java
│   │       └── WithdrawalTransaction.java
│   ├── repository/
│   │   └── TransactionRepository.java
│   └── validation/
│       └── DifferentAccountsValidator.java

├── security/            # Security Domain
│   ├── model/
│   │   ├── PasswordResetToken.java
│   │   └── dto/
│   │       ├── PasswordResetDTO.java
│   │       └── ChangePasswordRequest.java
│   ├── service/
│   │   ├── PasswordService.java
│   │   ├── PasswordResetService.java
│   │   ├── TokenHashingService.java
│   │   └── CustomUserDetailsService.java
│   └── config/
│       └── SecurityConfig.java

├── notification/        # Notification Domain
│   ├── email/
│   │   ├── EmailService.java
│   │   ├── EmailSender.java
│   │   └── ResendEmailSender.java
│   └── template/
│       ├── EmailTemplate.java
│       ├── EmailTemplateProvider.java
│       └── templates/
│           ├── PasswordResetEmailTemplate.java
│           └── WelcomeEmailTemplate.java

├── presentation/        # Presentation Layer
│   ├── dashboard/
│   │   ├── controller/
│   │   │   ├── DashboardController.java
│   │   │   ├── SettingsController.java
│   │   │   └── TransactionHistoryController.java
│   │   ├── service/
│   │   │   ├── DashboardService.java
│   │   │   └── SettingsService.java
│   │   └── dto/
│   │       ├── DashboardDTO.java
│   │       └── SettingsDTO.java
│   ├── auth/
│   │   ├── controller/
│   │   │   ├── LoginController.java
│   │   │   ├── RegistrationController.java
│   │   │   └── PasswordResetController.java
│   │   └── dto/
│   │       └── LoginDTO.java
│   └── shared/
│       └── layout/

├── shared/             # Shared Infrastructure
│   ├── config/
│   │   ├── WebConfig.java
│   │   └── AsyncConfiguration.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── all custom exceptions...
│   └── util/
│       ├── JwtUtil.java
│       ├── IbanGenerator.java
│       ├── LoggingService.java
│       └── AccountLockManager.java

└── export/             # Export Module
    ├── TransactionExporter.java
    ├── CsvTransactionExporter.java
    └── PdfTransactionExporter.java
```

### Key Points
1. Core domains contain their complete vertical slice (model, service, repository, validation)
2. Presentation layer is separate and organized by user interface areas
3. Shared code is properly isolated
4. Clear separation between business logic and presentation
5. Infrastructure concerns (security, notification, export) are separate modules

### Next Steps
1. Create the directory structure
2. Move files to their new locations
3. Update package declarations and imports
4. Verify all tests still pass
5. Document any breaking changes



## [01.03.2025] Simple Rate Limiting Implementation Using Spring Boot

### Context
Need a simple solution for limiting login and password reset attempts. Will use Spring Boot's built-in capabilities without additional infrastructure.

### Solution
```java
@Component
public class SimpleRateLimiter {
    private final LoadingCache<String, Integer> attemptsCache;
    
    public SimpleRateLimiter() {
        attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, Integer>() {
                public Integer load(String key) { return 0; }
            });
    }
    
    public boolean isBlocked(String key) {
        int attempts = attemptsCache.get(key);
        return attempts >= getMaxAttempts(key);
    }
    
    public void recordAttempt(String key) {
        int attempts = attemptsCache.get(key);
        attemptsCache.put(key, attempts + 1);
    }
    
    private int getMaxAttempts(String key) {
        return key.contains("login") ? 10 : 5; // 10 for login, 5 for reset
    }
}
```

### Usage Example
```java
@Service
public class SecurityService {
    private final SimpleRateLimiter rateLimiter;
    
    public void checkRateLimit(HttpServletRequest request, String type) {
        String ip = request.getRemoteAddr();
        String key = ip + ":" + type;
        
        if (rateLimiter.isBlocked(key)) {
            throw new TooManyAttemptsException();
        }
        rateLimiter.recordAttempt(key);
    }
}
```

### Benefits
1. Simple implementation - single class
2. No external dependencies
3. Automatic cleanup after 1 hour
4. Easy to understand and maintain

### Implementation Steps
1. Add SimpleRateLimiter class
2. Inject into security services
3. Add to login and password reset flows

## [01.03.2025] IP-Based Rate Limiting Design Using Spring Security

### Context
Need to implement IP-based rate limiting for password reset and login attempts to prevent brute force attacks. After analysis, we can leverage Spring Security's built-in features instead of implementing a custom Redis solution.

### Spring Security Solution

#### 1. Using Built-in Rate Limiting
Spring Security provides built-in support for rate limiting through its login failure handling:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .formLogin()
                .failureHandler(authenticationFailureHandler())
            .and()
            .rememberMe()
            .and()
            .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/login?expired");
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    private final LoginAttemptService loginAttemptService;
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        loginAttemptService.loginFailed(ip);
        
        if (loginAttemptService.isBlocked(ip)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
                "Too many login attempts. Please try again later");
            return;
        }
        
        super.onAuthenticationFailure(request, response, exception);
    }
}
```

#### 2. Login Attempt Tracking Service

```java
@Service
public class LoginAttemptService {
    private final int MAX_ATTEMPT = 10;
    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) {
                    return 0;
                }
            });
    }

    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
```

#### 3. Password Reset Rate Limiting

```java
@Component
public class PasswordResetRateLimiter {
    private final int MAX_RESET_ATTEMPTS = 5;
    private LoadingCache<String, Integer> resetAttemptsCache;

    public PasswordResetRateLimiter() {
        resetAttemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) {
                    return 0;
                }
            });
    }

    @PostFilter("@passwordResetRateLimiter.checkRateLimit(#ip)")
    public boolean checkRateLimit(String ip) {
        int attempts = getAttempts(ip);
        return attempts < MAX_RESET_ATTEMPTS;
    }

    private int getAttempts(String ip) {
        try {
            return resetAttemptsCache.get(ip);
        } catch (ExecutionException e) {
            return 0;
        }
    }

    public void incrementAttempts(String ip) {
        int attempts = getAttempts(ip);
        resetAttemptsCache.put(ip, attempts + 1);
    }
}
```

### Benefits
1. No additional infrastructure needed (no Redis)
2. Uses Spring Security's mature and tested framework
3. In-memory solution with automatic cleanup
4. Easy to implement and maintain
5. Built-in integration with Spring Security events

### Implementation Steps
1. Add rate limiting configuration to SecurityConfig
2. Implement CustomAuthenticationFailureHandler
3. Create LoginAttemptService for tracking login attempts
4. Implement PasswordResetRateLimiter for reset attempts
5. Add rate limit interceptors to relevant endpoints
6. Implement proper error responses

### Monitoring
1. Add metrics for:
   - Failed login attempts per IP
   - Password reset attempts per IP
   - Number of blocked IPs
2. Configure alerts for suspicious patterns
3. Log security events for audit trail

### Security Considerations
1. Use X-Forwarded-For header handling for proper IP detection
2. Consider implementing IP whitelist for internal services
3. Add proper logging for security audit
4. Implement graceful degradation if cache service fails


## 28.02.2025 - Email System Refactoring

**Context:**
System emaili nie spełniał zasad SOLID, łącząc logikę szablonów i wysyłania w jednej klasie. Potrzebna była refaktoryzacja.

**Decision:**
1. Rozdzielić odpowiedzialności na niezależne komponenty:
   - EmailSender - interfejs dla wysyłania emaili
   - ResendEmailSender - konkretna implementacja dla Resend API
   - EmailTemplateProvider - interfejs dla zarządzania szablonami
   - DefaultEmailTemplateProvider - implementacja szablonów HTML
   - EmailContent - klasa dla zawartości emaila
   - EmailTemplate - abstrakcyjna klasa bazowa dla szablonów
   - Konkretne implementacje szablonów

**Rationale:**
1. Single Responsibility Principle:
   - Każda klasa ma jedną odpowiedzialność
   - EmailSender tylko wysyła emaile
   - EmailTemplateProvider tylko zarządza szablonami

2. Open/Closed Principle:
   - Łatwe dodawanie nowych szablonów przez dziedziczenie
   - Możliwość dodania nowych implementacji wysyłki emaili

3. Liskov Substitution Principle:
   - Wszystkie implementacje EmailSender są wymienne
   - Wszystkie szablony mogą być używane wymiennie

4. Interface Segregation Principle:
   - Małe, specyficzne interfejsy
   - Klienci zależą tylko od potrzebnych metod

5. Dependency Inversion Principle:
   - EmailService zależy od abstrakcji, nie konkretnych implementacji
   - Łatwe testowanie dzięki możliwości mockowania

**Implementation:**
1. Interfejsy:
   - EmailSender dla wysyłki emaili
   - EmailTemplateProvider dla szablonów

2. Klasy abstrakcyjne:
   - EmailTemplate jako baza dla szablonów
   - TemplateVariables do konfiguracji szablonów

3. Implementacje:
   - ResendEmailSender dla Resend API
   - DefaultEmailTemplateProvider dla szablonów HTML
   - Konkretne szablony dla różnych typów emaili

4. Testy:
   - Mocki interfejsów dla łatwego testowania
   - Sprawdzanie interakcji między komponentami

**Results:**
1. Łatwiejsze testowanie dzięki interfejsom
2. Możliwość łatwej wymiany implementacji
3. Czytelniejszy i bardziej modularny kod
4. Łatwiejsze dodawanie nowych funkcjonalności

**Future Considerations:**
1. Dodanie wsparcia dla wielu języków w szablonach
2. Implementacja systemu śledzenia emaili
3. Możliwość personalizacji wyglądu przez użytkowników


## [2025-02-28] - Password Reset System Implementation Completion

### Context
The password reset system implementation has been completed successfully using SHA-256 for token hashing. The system has been tested and is working correctly.

### Key Decisions

#### 1. Finalization of Token Hashing Strategy
**Decision**: Continue using SHA-256 for token hashing rather than migrating to Argon2id

**Implementation Details**:
- Maintain the existing 32-byte secure random token generation
- Continue using SHA-256 for token hashing
- Store hashed tokens in the token_hash column
- Ensure proper token validation

**Rationale**:
- SHA-256 provides sufficient security for the current use case
- Implementation is already complete and working correctly
- No need for additional complexity at this time
- Focus on stability and reliability of the existing solution

#### 2. System Testing and Validation
**Decision**: Comprehensive testing confirms system readiness

**Testing Approach**:
- Unit tests for TokenHashingService and PasswordResetTokenService
- Integration tests for the complete password reset flow
- Security testing for rate limiting and token validation
- End-to-end testing with email delivery

**Rationale**:
- Ensures all components work together correctly
- Validates security measures are effective
- Confirms user experience meets requirements
- Provides confidence in system reliability

#### 3. Deployment Strategy
**Decision**: Direct deployment to production

**Implementation Details**:
- Deploy all components simultaneously
- Monitor system performance and security
- Provide support for any issues that arise

**Rationale**:
- System is well-tested and ready for production
- Simple deployment minimizes transition issues
- Immediate availability of the password reset functionality
- Clear monitoring plan ensures quick response to any issues

## [2025-02-27] - Token Hashing Security Enhancement

### Context
Current implementation stores plain tokens in the database, which poses a security risk if the database is compromised. We need to enhance security by implementing token hashing while maintaining existing functionality.

### Key Decisions

#### 1. Token Generation and Hashing
**Decision**: Implement secure token generation and hashing mechanism

**Implementation Details**:
- Use SecureRandom for token generation (32 bytes)
- Implement SHA-256 for token hashing
- Store only hashed tokens in database
- Implement proper token validation

**Rationale**:
- Protect against database breaches
- Follow security best practices
- Maintain current functionality
- Enable future security enhancements

#### 2. Database Schema Changes
**Decision**: Implement clean schema update

**Changes**:
- Replace token column with token_hash column
- Update unique constraints
- Remove existing tokens

**Rationale**:
- Clean implementation without legacy support
- Simplified database structure
- Clear security boundaries
- Fresh start with secure implementation

#### 3. Implementation Strategy
**Decision**: Direct replacement with token removal

**Steps**:
1. Backup existing schema (for rollback)
2. Remove all existing tokens
3. Update schema and deploy new code
4. Start fresh with hashed tokens

**Rationale**:
- Clean and straightforward implementation
- No need for migration complexity
- Immediate security benefits
- Minimal deployment complexity

#### 4. Token Generation and Hashing Process
**Decision**: Implement secure token generation and hashing mechanism

**Process Steps**:
1. Token Generation:
   - Generate 32 random bytes using SecureRandom
   - Base64URL encode the bytes to create the plain token
   - This token is sent to the user via email

2. Token Hashing:
   - Use SHA-256 algorithm for hashing
   - Store only the hash in the database
   - Never store or log the plain token

3. Token Validation:
   - Hash the received token using same parameters
   - Compare with stored hash
   - Prevent token reuse

**Implementation Example**:
```java
public class TokenHashingService {
    private static final int TOKEN_LENGTH = 32;  // bytes
    
    public String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        new SecureRandom().nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
```

**Security Considerations**:
- Token entropy: 256 bits (32 bytes) of randomness
- Secure hashing with SHA-256
- No plain token storage
- Token expiration and usage tracking

**Rationale**:
- High security against brute force
- Protection against database breaches
- Clean separation of tokens and hashes
- Industry standard approach

## [2025-02-27] - Password Reset System Implementation Day 2

### Context
Day 2 of the accelerated password reset system implementation, focusing on security measures and deployment preparation.

### Key Decisions

#### 1. Security Implementation Priority
**Decision**: Implement core security features before deployment

**Implementation Order**:
1. Rate limiting
2. Security monitoring
3. Token validation
4. Password validation

**Rationale**:
- Security cannot be compromised despite time constraints
- Core security features must be properly tested
- Monitoring needs to be in place before deployment

#### 2. Deployment Strategy
**Decision**: Staged deployment approach

**Steps**:
1. Deploy to staging environment
2. Run security tests
3. Monitor for 1 hour
4. If successful, deploy to production

**Rationale**:
- Allows for final validation in a production-like environment
- Provides opportunity to catch security issues
- Minimizes risk to production environment

#### 3. Monitoring Setup
**Decision**: Implement essential monitoring first

**Key Metrics**:
- Reset attempt counts
- Success/failure rates
- Response times
- Error rates

**Rationale**:
- Focus on critical metrics for security
- Enable quick detection of issues
- Support future improvements

## [2025-02-25] - Konfiguracja Deploymentu na Heroku

### Kontekst
Aplikacja Spring Boot wymaga skonfigurowania automatycznego deployu na platformie Heroku. Baza danych jest już skonfigurowana na zewnętrznym serwisie.

### Główne Decyzje

#### 1. Konfiguracja Systemu
**Decyzja**: Minimalna konfiguracja niezbędna do uruchomienia na Heroku

**Wymagane Pliki**:
- `system.properties`: Określenie wersji Javy (21)
- `Procfile`: Definicja sposobu uruchamiania aplikacji

**Uzasadnienie**:
- Heroku wymaga jawnego określenia wersji Javy
- Procfile zapewnia prawidłowe uruchomienie aplikacji

#### 2. Proces Deploymentu
**Decyzja**: Wykorzystanie GitHub Actions i Heroku CLI

**Kroki Wdrożenia**:
1. Utworzenie aplikacji na Heroku
2. Połączenie z repozytorium GitHub
3. Konfiguracja zmiennych środowiskowych
4. Uruchomienie automatycznego deploymentu

**Uzasadnienie**:
- Automatyzacja procesu deploymentu
- Integracja z istniejącym flow CI/CD
- Łatwe zarządzanie wersjami

### Plan Implementacji

#### Krok 1: Konfiguracja Podstawowa
```bash
# Logowanie do Heroku
heroku login

# Tworzenie aplikacji
heroku create bankapp-prod

# Konfiguracja środowiska
heroku config:set SPRING_PROFILES_ACTIVE=prod
```

#### Krok 2: Continuous Deployment
1. Połączenie repozytorium GitHub z Heroku przez Dashboard
2. Konfiguracja automatycznego deploymentu dla brancha main
3. Konfiguracja review apps (opcjonalnie)

### Metryki Sukcesu
1. Aplikacja uruchamia się poprawnie na Heroku
2. Automatyczny deployment działa po push na main
3. Aplikacja poprawnie łączy się z zewnętrzną bazą danych
4. Logi pokazują prawidłowe działanie aplikacji

### Monitoring i Utrzymanie
1. Regularne sprawdzanie logów Heroku
2. Monitoring zużycia zasobów
3. Backup konfiguracji

## [2024-02-24] - Przyspieszony Plan Implementacji Systemu Resetowania Hasła

### Kontekst
Potrzeba przyspieszenia implementacji systemu resetowania hasła z pierwotnego planu 4-tygodniowego do 2 dni, zachowując kluczowe aspekty bezpieczeństwa i funkcjonalności.

### Główne Decyzje

#### 1. Ograniczenie Zakresu
**Decyzja**: Skupienie się na MVP (Minimum Viable Product)

**Uzasadnienie**:
- Czas implementacji ograniczony do 2 dni
- Konieczność priorytetyzacji kluczowych funkcjonalności
- Możliwość późniejszego rozszerzenia systemu

**Konsekwencje**:
- Szybsze wdrożenie podstawowej funkcjonalności
- Mniejsza złożoność początkowego rozwiązania
- Łatwiejsze testowanie i deployment

#### 2. Architektura API
**Decyzja**: Ograniczenie do 2 głównych endpointów:
- `/api/password/reset-request`
- `/api/password/reset-complete`

**Uzasadnienie**:
- Minimalna niezbędna funkcjonalność
- Uproszczenie logiki biznesowej
- Zmniejszenie powierzchni ataku

**Implementacja**:
```java
@RestController
@RequestMapping("/api/password")
public class PasswordResetController {
    @PostMapping("/reset-request")     // Generacja i wysyłka tokenu
    @PostMapping("/reset-complete")    // Walidacja tokenu i zmiana hasła
}
```

#### 3. Bezpieczeństwo
**Decyzja**: Implementacja podstawowego ale solidnego systemu zabezpieczeń

**Kluczowe Elementy**:
1. Rate Limiting:
   - 3 próby/godzinę per email
   - 5 prób/godzinę per IP

2. Walidacja:
   - Sprawdzanie tokenu JWT
   - Walidacja emaila
   - Wymagania dla hasła

3. Monitoring:
   - Logowanie prób resetowania
   - Podstawowe alerty bezpieczeństwa

**Uzasadnienie**:
- Zachowanie kluczowych aspektów bezpieczeństwa
- Możliwość szybkiego wdrożenia
- Łatwe rozszerzenie w przyszłości

#### 4. Frontend
**Decyzja**: Minimalistyczny ale funkcjonalny interfejs

**Implementacja**:
1. Formularze:
   - reset-password.html
   - new-password.html

2. Walidacja:
   - Podstawowa walidacja JS
   - Komunikaty błędów
   - Spójny wygląd z istniejącym UI

**Uzasadnienie**:
- Szybka implementacja
- Zachowanie spójności z istniejącym interfejsem
- Dobry UX mimo prostoty

#### 5. Email
**Decyzja**: Prosta integracja z resend.com

**Szczegóły**:
- Podstawowy szablon
- Retry mechanizm (max 3 próby)
- Logowanie statusów wysyłki

**Uzasadnienie**:
- Szybka implementacja
- Niezawodność wysyłki
- Możliwość późniejszego rozszerzenia

=======
## [2024-02-24] - Przyspieszony Plan Implementacji Systemu Resetowania Hasła

### Kontekst
Potrzeba przyspieszenia implementacji systemu resetowania hasła z pierwotnego planu 4-tygodniowego do 2 dni, zachowując kluczowe aspekty bezpieczeństwa i funkcjonalności.

### Główne Decyzje

#### 1. Ograniczenie Zakresu
**Decyzja**: Skupienie się na MVP (Minimum Viable Product)

**Uzasadnienie**:
- Czas implementacji ograniczony do 2 dni
- Konieczność priorytetyzacji kluczowych funkcjonalności
- Możliwość późniejszego rozszerzenia systemu

**Konsekwencje**:
- Szybsze wdrożenie podstawowej funkcjonalności
- Mniejsza złożoność początkowego rozwiązania
- Łatwiejsze testowanie i deployment

#### 2. Architektura API
**Decyzja**: Ograniczenie do 2 głównych endpointów:
- `/api/password/reset-request`
- `/api/password/reset-complete`

**Uzasadnienie**:
- Minimalna niezbędna funkcjonalność
- Uproszczenie logiki biznesowej
- Zmniejszenie powierzchni ataku

**Implementacja**:
```java
@RestController
@RequestMapping("/api/password")
public class PasswordResetController {
    @PostMapping("/reset-request")     // Generacja i wysyłka tokenu
    @PostMapping("/reset-complete")    // Walidacja tokenu i zmiana hasła
}
```

#### 3. Bezpieczeństwo
**Decyzja**: Implementacja podstawowego ale solidnego systemu zabezpieczeń

**Kluczowe Elementy**:
1. Rate Limiting:
   - 3 próby/godzinę per email
   - 5 prób/godzinę per IP

2. Walidacja:
   - Sprawdzanie tokenu JWT
   - Walidacja emaila
   - Wymagania dla hasła

3. Monitoring:
   - Logowanie prób resetowania
   - Podstawowe alerty bezpieczeństwa

**Uzasadnienie**:
- Zachowanie kluczowych aspektów bezpieczeństwa
- Możliwość szybkiego wdrożenia
- Łatwe rozszerzenie w przyszłości

#### 4. Frontend
**Decyzja**: Minimalistyczny ale funkcjonalny interfejs

**Implementacja**:
1. Formularze:
   - reset-password.html
   - new-password.html

2. Walidacja:
   - Podstawowa walidacja JS
   - Komunikaty błędów
   - Spójny wygląd z istniejącym UI

**Uzasadnienie**:
- Szybka implementacja
- Zachowanie spójności z istniejącym interfejsem
- Dobry UX mimo prostoty

#### 5. Email
**Decyzja**: Prosta integracja z resend.com

**Szczegóły**:
- Podstawowy szablon
- Retry mechanizm (max 3 próby)
- Logowanie statusów wysyłki

**Uzasadnienie**:
- Szybka implementacja
- Niezawodność wysyłki
- Możliwość późniejszego rozszerzenia
