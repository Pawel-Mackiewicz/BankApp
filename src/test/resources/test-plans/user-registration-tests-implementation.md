# Implementacja testów integracyjnych dla rejestracji użytkownika

## Krok 1: Podstawowa konfiguracja testów

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserRegistrationIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @MockBean
    private EmailService emailService;
    
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        accountRepository.deleteAll();
        transactionRepository.deleteAll();
    }
}
```

### Klasa pomocnicza dla testów

```java
class TestUserRegistrationDtoBuilder {
    public static UserRegistrationDto createValid() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFirstname("Jan");
        dto.setLastname("Kowalski");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setPESEL("90010112345");
        dto.setEmail("jan.kowalski@example.com");
        dto.setPhoneNumber("+48123456789");
        dto.setPassword("Test123!@#");
        dto.setConfirmPassword("Test123!@#");
        return dto;
    }
    
    public static UserRegistrationDto createInvalid() {
        UserRegistrationDto dto = createValid();
        dto.setFirstname("Jan123"); // Nieprawidłowe imię z cyframi
        return dto;
    }
}
```

## Krok 2: Implementacja testów

### Test 1: Scenariusz pozytywny - poprawna rejestracja

```java
@Test
void shouldSuccessfullyRegisterNewUser() {
    // given
    UserRegistrationDto dto = TestUserRegistrationDtoBuilder.createValid();
    
    // when
    ResponseEntity<User> response = restTemplate.postForEntity(
        "/api/register",
        dto,
        User.class
    );
    
    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    
    User createdUser = response.getBody();
    assertThat(createdUser.getId()).isNotNull();
    
    // Sprawdzenie czy konto zostało utworzone
    Optional<Account> account = accountRepository.findByUserId(createdUser.getId());
    assertThat(account).isPresent();
    
    // Sprawdzenie czy transakcja powitalna została wykonana
    List<Transaction> transactions = transactionRepository.findByToAccountId(account.get().getId());
    assertThat(transactions)
        .hasSize(1)
        .first()
        .satisfies(transaction -> {
            assertThat(transaction.getAmount()).isEqualByComparingTo(new BigDecimal("1000"));
            assertThat(transaction.getTitle()).isEqualTo("Welcome bonus");
        });
    
    // Sprawdzenie wysłania emaila
    verify(emailService).sendWelcomeEmail(
        eq(dto.getEmail()),
        eq(createdUser.getFullName()),
        eq(createdUser.getUsername())
    );
}
```

### Test 2: Scenariusze negatywne

```java
@Test
void shouldRejectRegistrationWithInvalidFirstname() {
    // given
    UserRegistrationDto dto = TestUserRegistrationDtoBuilder.createInvalid();
    
    // when
    ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
        "/api/register",
        dto,
        ErrorResponse.class
    );
    
    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getMessage()).contains("Invalid first name");
    
    // Sprawdzenie czy użytkownik nie został utworzony
    assertThat(userRepository.count()).isZero();
    assertThat(accountRepository.count()).isZero();
    assertThat(transactionRepository.count()).isZero();
}

@Test
void shouldRejectDuplicateEmail() {
    // given
    UserRegistrationDto dto1 = TestUserRegistrationDtoBuilder.createValid();
    UserRegistrationDto dto2 = TestUserRegistrationDtoBuilder.createValid();
    
    // when
    restTemplate.postForEntity("/api/register", dto1, User.class);
    ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
        "/api/register",
        dto2,
        ErrorResponse.class
    );
    
    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getMessage()).contains("Email already exists");
    
    // Sprawdzenie czy tylko pierwszy użytkownik został utworzony
    assertThat(userRepository.count()).isEqualTo(1);
}
```

## Klasy pomocnicze

### ErrorResponse.java
```java
@Data
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private int status;
}
```

## Konfiguracja application-test.properties

```properties
spring.datasource.url=jdbc:tc:postgresql:14-alpine:///testdb
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver
spring.jpa.hibernate.ddl-auto=create-drop

# Wyłączenie rzeczywistego wysyłania maili w testach
spring.mail.host=localhost
spring.mail.port=3025
```

## Następne kroki
1. Implementacja pozostałych scenariuszy negatywnych:
   - Nieprawidłowy PESEL
   - Nieprawidłowy format telefonu
   - Nieprawidłowy format hasła
   - Niezgodność hasła z potwierdzeniem
2. Testy walidacji wieku (adnotacja @Adult)
3. Testy wydajnościowe przy równoczesnej rejestracji
4. Testy integracji z innymi modułami (np. wysyłka maili)