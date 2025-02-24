# System Patterns

## Architektura Warstw

### 1. Warstwa Prezentacji (UI)
- Pattern: MVC
- Implementacja:
  * Kontrolery Spring MVC dla obsługi żądań HTTP
  * Thymeleaf dla renderowania widoków
  * JavaScript dla interakcji po stronie klienta
  * Walidacja dwustronna (client-side i server-side)

### 2. Warstwa Biznesowa (Services)
- Pattern: Service Layer
- Implementacja:
  * Serwisy Spring dla logiki biznesowej
  * Dependency Injection dla luźnego powiązania komponentów
  * Transaction management dla operacji ACID
  * Event-driven dla operacji asynchronicznych

### 3. Warstwa Dostępu do Danych
- Pattern: Repository
- Implementacja:
  * Spring Data JPA dla operacji bazodanowych
  * Hibernate jako ORM
  * Custom queries dla złożonych operacji

## Design Patterns

### 1. Repository Pattern
```java
// Przykład dla PasswordResetToken
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByExpiryDateLessThan(LocalDateTime now);
}
```

### 2. Service Pattern
```java
@Service
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository repository;
    private final JwtUtil jwtUtil;
    
    // Constructor injection
    public PasswordResetTokenService(PasswordResetTokenRepository repository, JwtUtil jwtUtil) {
        this.repository = repository;
        this.jwtUtil = jwtUtil;
    }
}
```

### 3. DTO Pattern
```java
public class PasswordResetRequestDTO {
    private String email;
    // Getters, setters, validation
}
```

### 4. Builder Pattern
```java
// Używany w konstruowaniu złożonych obiektów
public class TransactionBuilder {
    private Transaction transaction = new Transaction();
    
    public TransactionBuilder withAmount(BigDecimal amount) {
        transaction.setAmount(amount);
        return this;
    }
    // ...
}
```

## Security Patterns

### 1. Token-Based Authentication
- Implementation: JWT dla tokenów resetowania hasła
- Bezpieczne generowanie i walidacja
- Ograniczony czas życia tokenów
- Jednorazowe użycie

### 2. Rate Limiting
```java
@Service
public class SecurityService {
    private final RateLimiter rateLimiter;
    
    public void checkRateLimit(String ipAddress) {
        if (!rateLimiter.tryAcquire()) {
            throw new TooManyRequestsException();
        }
    }
}
```

### 3. Validation Pattern
```java
@Valid
@RequestBody PasswordResetRequestDTO request
```

## Integration Patterns

### 1. Email Service Integration
- Pattern: Gateway Pattern
- Implementation: Abstrakcja dla serwisu email
  * Łatwa zmiana dostawcy (np. z resend.com na inny)
  * Retry mechanism dla nieudanych prób
  * Asynchroniczne wysyłanie

### 2. Event-Driven Pattern
```java
@Service
public class EmailService {
    @Async
    public CompletableFuture<Void> sendPasswordResetEmail(String to, String token) {
        // Asynchroniczne wysyłanie emaila
    }
}
```

## Testing Patterns

### 1. Unit Testing
```java
@Test
public void testPasswordResetTokenCreation() {
    // Given
    String email = "test@example.com";
    
    // When
    PasswordResetToken token = service.createToken(email);
    
    // Then
    assertNotNull(token);
    assertEquals(email, token.getUser().getEmail());
}
```

### 2. Integration Testing
```java
@SpringBootTest
public class PasswordResetIntegrationTest {
    @Autowired
    private PasswordResetTokenService service;
    
    @Test
    public void testCompleteResetFlow() {
        // Test pełnego flow resetowania hasła
    }
}
```

## Error Handling Pattern

### 1. Global Exception Handler
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        // Obsługa błędu
    }
}
```

## Monitoring Pattern

### 1. Logging Pattern
```java
@Slf4j
public class PasswordResetTokenService {
    public void createToken(String email) {
        log.info("Creating password reset token for user: {}", email);
        // ...
        log.debug("Token created successfully");
    }
}
```

### 2. Metrics Collection
```java
@Component
public class SecurityMetrics {
    private final Counter passwordResetAttempts;
    private final Counter passwordResetSuccess;
    
    public void recordResetAttempt() {
        passwordResetAttempts.increment();
    }
}
```

## Best Practices

### 1. Code Organization
- Spójny podział na pakiety
- Separacja odpowiedzialności
- Czytelne nazewnictwo
- Dokumentacja kluczowych komponentów

### 2. Security
- Walidacja wszystkich inputów
- Bezpieczne przechowywanie danych
- Audyt operacji bezpieczeństwa
- Rate limiting dla krytycznych operacji

### 3. Performance
- Asynchroniczne operacje gdzie możliwe
- Efektywne wykorzystanie cache
- Optymalizacja zapytań bazodanowych
- Monitoring wydajności

### 4. Maintainability
- Jasna dokumentacja
- Testy jednostkowe i integracyjne
- Czysty kod zgodny z SOLID
- Łatwa rozszerzalność