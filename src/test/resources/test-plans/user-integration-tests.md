# Plan testów integracyjnych dla modułu User

## 1. Testy rejestracji użytkownika (/register endpoint)

### 1.1 Scenariusz pozytywny
- **Test:** Pełna rejestracja użytkownika
- **Oczekiwane kroki:**
  1. Utworzenie użytkownika w bazie danych
  2. Utworzenie konta bankowego dla użytkownika
  3. Wykonanie transakcji powitalnej (1000 PLN)
  4. Wysłanie emaila powitalnego
- **Warunki sukcesu:**
  - Użytkownik zapisany w bazie
  - Konto bankowe utworzone i powiązane z użytkownikiem
  - Transakcja powitalna zarejestrowana
  - Email powitalny wysłany

### 1.2 Scenariusze negatywne
- **Test:** Rejestracja z nieprawidłowymi danymi
  - Imię zawierające cyfry
  - Nazwisko zawierające znaki specjalne
  - Duplikat adresu email
  - Nieprawidłowy PESEL
  - Nieprawidłowy numer telefonu

- **Test:** Obsługa błędów integracyjnych
  - Błąd podczas tworzenia konta bankowego
  - Błąd podczas wykonywania transakcji powitalnej
  - Błąd podczas wysyłania emaila

## 2. Implementacja testów

### 2.1 Konfiguracja
```java
@SpringBootTest
class UserRegistrationIntegrationTest {
    @Autowired
    private UserRegistrationService userRegistrationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @MockBean
    private EmailService emailService;
}
2.2 Przykładowe scenariusze testowe
@Test
void shouldSuccessfullyRegisterNewUser() {
    // given
    UserRegistrationDto dto = createValidRegistrationDto();
    
    // when
    User registeredUser = userRegistrationService.registerUser(dto);
    
    // then
    assertThat(userRepository.findById(registeredUser.getId())).isPresent();
    assertThat(accountRepository.findByUserId(registeredUser.getId())).isPresent();
    assertThat(transactionRepository.findByToAccountId(registeredUser.getId()))
        .hasSize(1)
        .first()
        .matches(t -> t.getAmount().equals(new BigDecimal("1000")));
    verify(emailService).sendWelcomeEmail(any(), any(), any());
}

@Test
void shouldFailRegistrationWhenInvalidName() {
    // given
    UserRegistrationDto dto = createRegistrationDtoWithInvalidName();
    
    // when/then
    assertThrows(IllegalArgumentException.class, 
        () -> userRegistrationService.registerUser(dto));
}
3. Testy wydajnościowe
3.1 Scenariusze do przetestowania
Równoczesna rejestracja wielu użytkowników
Czas odpowiedzi endpointu rejestracji
Obciążenie bazy danych podczas rejestracji
4. Narzędzia i konfiguracja
4.1 Wymagane zależności
TestContainers dla bazy danych
WireMock dla mockowania zewnętrznych serwisów
MockMvc dla testów REST API
4.2 Profil testowy
# application-test.properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.datasource.url=jdbc:tc:postgresql:14-alpine:///testdb
5. Kolejne kroki
Implementacja podstawowego scenariusza rejestracji
Dodanie testów negatywnych
Implementacja testów wydajnościowych
Dodanie testów integracji z innymi modułami
