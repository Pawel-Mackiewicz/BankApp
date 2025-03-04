# Decision Log

## 2025-03-04 - Event Logger Implementation

**Context:**
Po ustaleniu struktury interfejsów dla systemu logowania eventów, potrzebujemy zdefiniować szczegóły implementacyjne i lokalizacji w projekcie.

**Decision:**

1. Struktura pakietów:


```
src/main/java/info/mackiewicz/bankapp/event/
  ├── logger/
  │   ├── EventLogger.java               // Główna fasada
  │   ├── AccountEventLogger.java        // Interfejs
  │   ├── SecurityEventLogger.java       // Interfejs
  │   └── TransactionEventLogger.java    // Interfejs
  └── impl/
      ├── Slf4jAccountEventLogger.java   // Implementacja
      ├── Slf4jSecurityEventLogger.java  // Implementacja
      └── Slf4jTransactionEventLogger.java // Implementacja
```

2. Implementacja loggerów:

```java
@Component
@Slf4j
public class Slf4jAccountEventLogger implements AccountEventLogger {
    @Override
    public void created(Integer accountId, Integer userId) {
        log.info("Account created: accountId={}, userId={}", accountId, userId);
    }
    // inne metody
}
```

**Rationale:**

- Czysta struktura pakietów z podziałem na interfejsy i implementacje
- Używanie standardowych komponentów Spring i Lombok
- Łatwe dodawanie nowych typów eventów
- Prosta migracja do bardziej zaawansowanego systemu w przyszłości

**Implementation:**

1. Utworzenie struktury pakietów
2. Implementacja interfejsów
3. Konfiguracja w Spring (@Component)
4. Wstrzyknięcie EventLoggera w serwisach

**Architectural Impact:**

- Modularny system logowania eventów
- Łatwa rozszerzalność
- Możliwość łatwej zmiany implementacji

## 2025-03-04 - Event Logging System Details

**Context:**
Po ustaleniu podstawowej struktury systemu logowania eventów, potrzebne jest doprecyzowanie szczegółów technicznych implementacji, w tym sposobu zapisu, retencji i zarządzania logami.

**Decision:**

1. Sposób zapisu logów:
   - Implementacja synchronicznego zapisu dla większej niezawodności
   - Jeden event = jedna linia JSON
   - Operacje zapisu chronione przez mechanizm blokad

2. Retencja logów:
   - Okres przechowywania: 30 dni
   - Automatyczne czyszczenie starszych logów przy starcie aplikacji
   - Zachowanie struktury katalogów nawet po usunięciu starych plików

3. Format nazewnictwa plików:
   - Katalogi kategorii: account/, security/, transaction/
   - Pliki dzienne: RRRR-MM-DD.log
   - Przykład: logs/account/2025-03-04.log

**Rationale:**

- Synchroniczny zapis zapewnia większą niezawodność i pewność zapisu
- 30-dniowy okres retencji balansuje między potrzebami audytu a zarządzaniem przestrzenią
- Struktura katalogów ułatwia nawigację i zarządzanie logami
- JSON zapewnia czytelność i łatwość parsowania

**Implementation:**

1. Mechanizm zapisu:

```java
public interface EventWriter {
    void writeEvent(Event event);
    void cleanup(); // Usuwanie logów starszych niż 30 dni
}

@Component
public class FileEventWriter implements EventWriter {
    private final Object writeLock = new Object();
    
    @Override
    public void writeEvent(Event event) {
        synchronized(writeLock) {
            // Implementacja zapisu
        }
    }
}
```

2. Scheduler czyszczenia:

```java
@Scheduled(cron = "0 0 0 * * *") // Codziennie o północy
public void cleanupOldLogs() {
    // Usuwanie logów starszych niż 30 dni
}
```

**Architectural Impact:**

- Prostszy model utrzymania dzięki jasno określonej polityce retencji
- Przewidywalne zużycie zasobów dzięki regularnemu czyszczeniu
- Możliwość łatwego rozszerzenia o mechanizmy archiwizacji w przyszłości

## 2025-03-04 - Event Logging System Architecture

**Context:**
Aplikacja wymaga ustrukturyzowanego systemu logowania eventów biznesowych. Obecnie istnieje podstawowa implementacja logowania w LoggingService, ale potrzebne jest bardziej kompleksowe rozwiązanie z kategoryzacją i agregacją eventów.

**Decision:**

1. Utworzenie nowego modułu event w głównej strukturze projektu
2. Implementacja wzorca Event Sourcing dla przechowywania historii eventów
3. Kategoryzacja eventów:
   - SECURITY (logowanie, próby dostępu, zmiany hasła)
   - ACCOUNT (tworzenie, modyfikacje kont)
   - TRANSACTION (przelewy, wpłaty, wypłaty)
   - SYSTEM (błędy systemowe, starty/stopu komponentów)
   - AUDIT (ważne operacje wymagające audytu)

**Rationale:**

- Centralizacja logiki związanej z eventami
- Łatwiejsze śledzenie i analiza operacji biznesowych
- Możliwość odtworzenia stanu systemu na podstawie historii eventów
- Lepsze wsparcie dla audytu i compliance
- Separacja logowania technicznego od biznesowego

**Implementation:**

1. Struktura pakietów:

```
src/main/java/info/mackiewicz/bankapp/event/
  ├── model/
  │   ├── Event.java
  │   ├── EventType.java
  │   └── EventCategory.java
  ├── service/
  │   ├── EventLogger.java
  │   └── EventStore.java
  └── repository/
      └── EventRepository.java
```

2. Główne interfejsy:

```java
public interface EventLogger {
    void logEvent(Event event);
    void logEvent(String message, EventType type, EventCategory category);
    List<Event> getEventsByCategory(EventCategory category);
}

public interface EventStore {
    void store(Event event);
    List<Event> getEvents(EventCategory category);
    List<Event> getEventsByDateRange(LocalDateTime start, LocalDateTime end);
}
```

**Architectural Impact:**

- Uporządkowana struktura logowania eventów biznesowych
- Łatwiejsza implementacja nowych typów eventów
- Możliwość rozbudowy o mechanizmy analizy i raportowania
- Lepsza separacja concerns między logiką biznesową a logowaniem

## 2025-03-03 - Account Class Interface Extraction

**Context:**
The Account class currently has multiple responsibilities including financial operations, owner information management, and IBAN handling. This violates the Single Responsibility Principle and makes the class less maintainable.

**Decision:**
Extract the following interfaces from the Account class:

1. `FinancialOperations` - For handling monetary transactions
2. `AccountOwnerInfo` - For managing owner-related information
3. `IbanHolder` - For IBAN-related operations

**Rationale:**

- Improved separation of concerns
- Better maintainability and testability
- More flexible system evolution
- Easier to implement new account types in the future
- Better compliance with SOLID principles

**Implementation:**

1. Create three new interfaces:

```java
public interface FinancialOperations {
    void deposit(BigDecimal amount);
    void withdraw(BigDecimal amount);
    boolean canWithdraw(BigDecimal amount);
    BigDecimal getBalance();
}

public interface AccountOwnerInfo {
    AccountOwnerDTO getOwnerDTO();
    Integer getOwnerId();
    User getOwner();
    void setOwner(User owner);
}

public interface IbanHolder {
    String getIban();
    String getFormattedIban();
}
```

2. Make Account class implement these interfaces:

```java
public class Account implements FinancialOperations, AccountOwnerInfo, IbanHolder {
    // existing implementation
}
```

**Architectural Impact:**

- More modular and flexible account system
- Easier to implement different types of accounts in the future
- Better testability through interface-based design
- Clearer system boundaries and responsibilities
