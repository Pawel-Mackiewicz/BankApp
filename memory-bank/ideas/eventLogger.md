# Event Logger System Design

## Overview
System logowania eventów biznesowych zaimplementowany jako beany Spring, używający SLF4J do zapisywania zdarzeń.

## Structure
```
src/main/java/info/mackiewicz/bankapp/event/
  ├── logger/
  │   ├── EventLogger.java               // Główna fasada
  │   ├── AccountEventLogger.java        // Interfejs
  │   ├── SecurityEventLogger.java       // Interfejs
  │   └── TransactionEventLogger.java    // Interfejs
  ├── impl/
  │   ├── Slf4jAccountEventLogger.java   // Implementacja
  │   ├── Slf4jSecurityEventLogger.java  // Implementacja
  │   └── Slf4jTransactionEventLogger.java // Implementacja
  ├── model/
  │   ├── EventMetadata.java            // Model dla metadanych
  │   └── EventSeverity.java            // Enum poziomów severity
  ├── config/
  │   └── EventLoggerConfig.java        // Konfiguracja Spring
  └── util/
      └── CorrelationIdGenerator.java   // Generator ID korelacji
```

## Interfaces

### AccountEventLogger
```java
public interface AccountEventLogger {
    void created(Integer accountId, Integer userId);
    void deleted(Integer accountId, Integer userId);
    void locked(Integer accountId);
    void unlocked(Integer accountId);
    void updated(Integer accountId, String field, String oldValue, String newValue);
    void balanceChanged(Integer accountId, BigDecimal oldBalance, BigDecimal newBalance);
}
```

### SecurityEventLogger
```java
public interface SecurityEventLogger {
    void loginAttempt(Integer userId, boolean success, String ipAddress);
    void passwordChanged(Integer userId);
    void accessDenied(Integer userId, String resource);
    void sessionStarted(Integer userId, String sessionId);
    void sessionEnded(Integer userId, String sessionId);
    void roleChanged(Integer userId, String oldRole, String newRole);
}
```

### TransactionEventLogger
```java
public interface TransactionEventLogger {
    void transfer(Integer sourceAccountId, Integer targetAccountId, BigDecimal amount);
    void deposit(Integer accountId, BigDecimal amount);
    void withdrawal(Integer accountId, BigDecimal amount);
    void transactionFailed(Integer accountId, String reason);
    void transactionRollback(Integer transactionId, String reason);
}
```

Main Facade
@Component
@RequiredArgsConstructor
public class EventLogger {
    private final AccountEventLogger accountEvents;
    private final SecurityEventLogger securityEvents;
    private final TransactionEventLogger transactionEvents;
    
    public AccountEventLogger account() {
        return accountEvents;
    }
    
    public SecurityEventLogger security() {
        return securityEvents;
    }
    
    public TransactionEventLogger transaction() {
        return transactionEvents;
    }
}

## Logback Configuration
```xml
<configuration>
    <!-- Appender dla eventów konta -->
    <appender name="ACCOUNT_EVENTS" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/account/events.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/account/%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
            <totalSizeCap>3GB</totalSizeCap>
        </rollingPolicy>
        <encoder class="ch.qos.logback.core.encoder.JsonEncoder">
            <jsonFormatter class="ch.qos.logback.contrib.json.classic.JsonLayout">
                <timestampFormat>yyyy-MM-dd'T'HH:mm:ss.SSSZ</timestampFormat>
                <jsonFormatter class="ch.qos.logback.contrib.jackson.JacksonJsonFormatter">
                    <prettyPrint>false</prettyPrint>
                </jsonFormatter>
            </jsonFormatter>
        </encoder>
    </appender>

    <!-- Similar appenders for security and transaction events -->
</configuration>
```

## Error Handling
```java
@Slf4j
public abstract class BaseEventLogger {
    private final ErrorHandler errorHandler;
    
    protected void logEvent(String message, Object... args) {
        try {
            doLogEvent(message, args);
        } catch (Exception e) {
            errorHandler.handleError("Failed to log event", e);
            // Fallback do standardowego loggera
            log.error("Event logging failed: {}", message, e);
        }
    }
}
```

## Testing
```java
@SpringBootTest
class AccountEventLoggerTest {
    @MockBean
    private CorrelationIdGenerator correlationIdGenerator;
    
    @Autowired
    private AccountEventLogger eventLogger;
    
    @Test
    void shouldLogAccountCreation() {
        // given
        when(correlationIdGenerator.generate()).thenReturn("test-123");
        
        // when
        eventLogger.created(1, 2);
        
        // then
        // Verify log file content
    }
}
```

## Usage Examples
```java
@Service
@RequiredArgsConstructor
public class AccountService {
    private final EventLogger eventLogger;
    private final CorrelationIdGenerator correlationIdGenerator;
    
    @Transactional
    public Account createAccount(Integer userId) {
        String correlationId = correlationIdGenerator.generate();
        try {
            Account account = accountFactory.createAccount(userId);
            eventLogger.account().created(account.getId(), userId);
            return account;
        } catch (Exception e) {
            eventLogger.account().failed("create", userId, e.getMessage());
            throw e;
        }
    }
}
```

## Log Format
```json
{
  "timestamp": "2025-03-04T16:55:06.123Z",
  "category": "ACCOUNT",
  "type": "CREATE",
  "severity": "INFO",
  "userId": 123,
  "metadata": {
    "accountId": "456",
    "ip": "192.168.1.1",
    "userAgent": "Mozilla/5.0...",
    "sessionId": "sess-789"
  },
  "message": "Created new account",
  "correlationId": "abc-123",
  "duration": 150,
  "status": "SUCCESS"
}
```

## Implementation Details
### ErrorHandling
- Retry mechanizm dla błędów zapisu
- Dead letter queue dla nieudanych zapisów
- Monitoring błędów przez metrics

### Performance
- Buforowanie zapisów w przypadku dużego obciążenia
- Batch processing dla niektórych typów eventów
- Asynchroniczne zapisywanie mniej krytycznych eventów

### Security
- Maskowanie wrażliwych danych
- Walidacja danych wejściowych
- Kontrola dostępu do logów

### Monitoring
- Metryki Prometheus dla ilości eventów
- Alerty przy przekroczeniu progów błędów
- Dashboard z wizualizacją aktywności

## Migration Plan
1. Wprowadzenie nowego systemu obok starego
2. Stopniowa migracja serwis po serwisie
3. Okres równoległego działania obu systemów
4. Wyłączenie starego systemu

## Future Improvements
1. Asynchroniczny zapis z kolejkowaniem
2. API REST do odczytu logów
3. Narzędzia analityczne
4. System archiwizacji
5. Integracja z systemami monitoringu
6. Eksport do formatów biznesowych