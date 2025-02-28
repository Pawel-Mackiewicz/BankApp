# System Patterns & Architecture

## Security Patterns [Updated: 2025-02-28]

### Token Security Pattern
```java
public class TokenSecurityPattern {
    // 1. Secure Token Generation
    private static final int TOKEN_LENGTH = 32;
    private final SecureRandom secureRandom = new SecureRandom();
    
    public String generateSecureToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    // 2. Token Hashing with SHA-256
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    // 3. Token Verification
    public boolean verifyToken(String providedToken, String storedHash) {
        String computedHash = hashToken(providedToken);
        return computedHash.equals(storedHash);
    }
}
```

### Token Entity Pattern
```java
@Entity
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean used = false;
    
    @Column
    private LocalDateTime usedAt;
    
    public TokenEntity(String hashedToken) {
        this.tokenHash = hashedToken;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = createdAt.plusMinutes(60);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isValid() {
        return !isExpired() && !used;
    }
    
    public void markAsUsed() {
        if (!isValid()) {
            throw new IllegalStateException("Token is not valid");
        }
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }
}
```

### Rate Limiting Pattern
```java
public class RateLimiter {
    private final Cache<String, RateLimitInfo> emailLimits = CacheBuilder.newBuilder()
        .expireAfterWrite(24, TimeUnit.HOURS)
        .build();
        
    private final Cache<String, RateLimitInfo> tokenLimits = CacheBuilder.newBuilder()
        .expireAfterWrite(15, TimeUnit.MINUTES)
        .build();
        
    private final Cache<String, RateLimitInfo> ipLimits = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build();
    
    @Value
    private static class RateLimitInfo {
        private int attempts;
        private LocalDateTime firstAttempt;
        private LocalDateTime lastAttempt;
    }
    
    public boolean isEmailAllowed(String email) {
        RateLimitInfo info = emailLimits.getIfPresent(email);
        if (info == null || isNextDay(info.getFirstAttempt())) {
            emailLimits.put(email, new RateLimitInfo(1, now(), now()));
            return true;
        }
        if (info.getAttempts() >= 3) {
            return false;
        }
        info = new RateLimitInfo(info.getAttempts() + 1, info.getFirstAttempt(), now());
        emailLimits.put(email, info);
        return true;
    }
    
    public boolean isTokenValidationAllowed(String tokenHash) {
        RateLimitInfo info = tokenLimits.getIfPresent(tokenHash);
        if (info == null) {
            tokenLimits.put(tokenHash, new RateLimitInfo(1, now(), now()));
            return true;
        }
        if (info.getAttempts() >= 5) {
            return false;
        }
        info = new RateLimitInfo(info.getAttempts() + 1, info.getFirstAttempt(), now());
        tokenLimits.put(tokenHash, info);
        return true;
    }
    
    public boolean isIpAllowed(String ip) {
        RateLimitInfo info = ipLimits.getIfPresent(ip);
        if (info == null) {
            ipLimits.put(ip, new RateLimitInfo(1, now(), now()));
            return true;
        }
        if (info.getAttempts() >= 10) {
            return false;
        }
        info = new RateLimitInfo(info.getAttempts() + 1, info.getFirstAttempt(), now());
        ipLimits.put(ip, info);
        return true;
    }
}
```

### Token Validation Pattern
```java
public class TokenValidationPattern {
    private final RateLimiter rateLimiter;
    private final SecurityAuditor auditor;
    private final TokenHashingService hashingService;
    
    public Optional<String> validateToken(String token) {
        String ip = getCurrentIp();
        
        // 1. IP-based rate limiting
        if (!rateLimiter.isIpAllowed(ip)) {
            auditor.logRateLimitExceeded("IP: " + ip);
            throw new RateLimitExceededException("Too many requests from this IP");
        }
        
        // 2. Token-based rate limiting
        String tokenHash = hashingService.hashToken(token);
        if (!rateLimiter.isTokenValidationAllowed(tokenHash)) {
            auditor.logRateLimitExceeded("Token validation limit exceeded");
            throw new RateLimitExceededException("Too many validation attempts for this token");
        }
        
        // 3. Token validation
        try {
            return tokenRepository.findByTokenHash(tokenHash)
                .filter(TokenEntity::isValid)
                .map(TokenEntity::getUserEmail);
        } catch (Exception e) {
            auditor.logValidationError(token, e);
            return Optional.empty();
        }
    }
}
```

### Security Auditing Pattern
```java
@Service
@Slf4j
public class SecurityAuditor {
    private final AlertService alertService;
    
    public void logTokenGeneration(String userEmail) {
        log.info("Password reset token generated for user: {}", userEmail);
    }
    
    public void logTokenValidation(String userEmail, boolean success) {
        if (success) {
            log.info("Password reset token successfully validated for user: {}", userEmail);
        } else {
            log.warn("Failed password reset token validation for user: {}", userEmail);
            checkForSuspiciousActivity(userEmail);
        }
    }
    
    public void logPasswordReset(String userEmail) {
        log.info("Password successfully reset for user: {}", userEmail);
    }
    
    public void logRateLimitExceeded(String identifier) {
        log.warn("Rate limit exceeded: {}", identifier);
        alertService.sendAlert(AlertLevel.WARNING, 
            "Rate limit exceeded for " + identifier);
    }
    
    public void logValidationError(String token, Exception e) {
        log.error("Error validating token: {}", e.getMessage());
    }
    
    private void checkForSuspiciousActivity(String userEmail) {
        // Check for patterns of suspicious activity
        // and trigger alerts if necessary
    }
}
```

## Dashboard UI Patterns [Updated: 2025-02-22 20:11]

### 1. IBAN Display
- IBANs are displayed in the "My Accounts" section of the dashboard.
- IBANs are now highlighted on hover using CSS `text-shadow`.
- IBANs are copyable by clicking on them.

### 2. Transfer Interface
- Tabbed interface for different transfer types
- Dynamic form validation
- Real-time balance and IBAN display
- Responsive design patterns

### 3. Form Validation Patterns
```javascript
// Real-time validation pattern
async function validateInput(input) {
    const value = input.value;
    const type = input.dataset.type;
    
    try {
        const response = await fetch(`/api/validate-${type}?value=${value}`);
        const data = await response.json();
        
        if (data.valid) {
            setValid(input);
        } else {
            setInvalid(input, data.message);
        }
    } catch (error) {
        setInvalid(input, 'Validation error');
    }
}
```

## Transaction System Patterns [Updated: 2025-02-22 20:11]

### 1. Struktura Transakcji
```java
public enum TransactionCategory {
    TRANSFER("Transfer"),   // przelewy
    DEPOSIT("Deposit"),     // wpłaty
    WITHDRAWAL("Withdrawal"), // wypłaty
    FEE("Fee")             // opłaty
}

public enum TransactionType {
    TRANSFER_OWN(
        TransactionCategory.TRANSFER,
        "Własne konto",
        true,   // wymaga IBAN
        0.0     // bez prowizji
    ),
    
    TRANSFER_INTERNAL(
        TransactionCategory.TRANSFER,
        "Przelew w banku",
        true,   // wymaga IBAN
        0.0     // bez prowizji
    ),
    
    TRANSFER_EXTERNAL(
        TransactionCategory.TRANSFER,
        "Przelew zewnętrzny",
        true,   // wymaga IBAN
        0.01    // prowizja 1%
    ),
    
    // Inne operacje
    DEPOSIT(...),
    WITHDRAWAL(...)
}
```

### 2. Strategie Transakcji
```java
// Interface dla wszystkich strategii
public interface TransactionStrategy {
    void process(Transaction transaction);
    void validate(Transaction transaction);
}

// Przykład implementacji dla przelewu
public class TransferTransaction implements TransactionStrategy {
    @Override
    public void process(Transaction transaction) {
        // Logika przelewu zależna od typu
        switch(transaction.getType()) {
            case TRANSFER_OWN: // logika przelewu własnego
            case TRANSFER_INTERNAL: // logika przelewu wewnętrznego
            case TRANSFER_EXTERNAL: // logika przelewu zewnętrznego
        }
    }
}
```

### 3. Filtrowanie Transakcji
Istniejący system filtrowania obsługuje:
- Filtrowanie po kategorii (np. "TRANSFER")
- Filtrowanie po konkretnym typie
- Filtrowanie po dacie
- Filtrowanie po kwocie
- Wyszukiwanie po tekście

```java
// Przykład użycia:
List<Transaction> filtered = filterService.filterTransactions(
    transactions,
    dateFrom,    // od kiedy
    dateTo,      // do kiedy
    "TRANSFER",  // kategoria lub typ
    minAmount,   // minimalna kwota
    maxAmount,   // maksymalna kwota
    searchQuery  // tekst do wyszukania
);
```

### 4. Walidacja
- Format IBAN w osobnej klasie IbanValidator
- Reguły biznesowe w TransactionService
- Walidacja na poziomie strategii transakcji
- Nowe wzorce walidacji:
  * Walidacja email przez AccountRepository
  * Walidacja dostępnych środków
  * Walidacja uprawnień do kont

### 5. Wzorce
- Strategy Pattern dla różnych typów transakcji
- Builder Pattern dla tworzenia transakcji
- Factory Method dla strategii transakcji
- Validator Pattern dla walidacji
- Observer Pattern dla aktualizacji UI
- Command Pattern dla operacji transferowych

### 6. Nowe Wzorce UI
```javascript
// Tab Management Pattern
class TabManager {
    constructor(tabContainer, contentContainer) {
        this.tabs = tabContainer.querySelectorAll('[data-tab]');
        this.contents = contentContainer.querySelectorAll('[data-content]');
        
        this.tabs.forEach(tab => {
            tab.addEventListener('click', () => this.switchTab(tab));
        });
    }
    
    switchTab(activeTab) {
        // Deaktywuj wszystkie
        this.tabs.forEach(tab => tab.classList.remove('active'));
        this.contents.forEach(content => content.classList.remove('active'));
        
        // Aktywuj wybrany
        activeTab.classList.add('active');
        const content = document.querySelector(
            `[data-content="${activeTab.dataset.tab}"]`
        );
        content.classList.add('active');
    }
}

// Form State Management Pattern
class FormStateManager {
    constructor(form) {
        this.form = form;
        this.state = {};
        this.validators = {};
        
        this.setupValidators();
        this.setupListeners();
    }
    
    async validate(field) {
        const validator = this.validators[field.name];
        if (validator) {
            return await validator(field.value);
        }
        return true;
    }
}