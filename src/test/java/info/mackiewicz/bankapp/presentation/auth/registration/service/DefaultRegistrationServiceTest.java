package info.mackiewicz.bankapp.presentation.auth.registration.service;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationMapper;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.testutils.TestAccountBuilder;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultRegistrationServiceTest {

    @Value("${bankapp.registration.WelcomeBonusAmount:1000}")
    private BigDecimal defaultWelcomeBonusAmount;

    @Mock
    private UserService userService;

    @Mock
    private RegistrationMapper registrationMapper;

    @Mock
    private AccountService accountService;

    @Mock
    private BonusGrantingService bonusGrantingService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private DefaultRegistrationService registrationService;

    @NotNull
    private static RegistrationRequest initializeRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("StrongP@ss123");
        request.setConfirmPassword("StrongP@ss123");
        request.setPesel("12345678901");
        request.setPhoneNumber("+48123456789");
        request.setDateOfBirth(LocalDate.parse("1997-07-07"));
        return request;
    }

    @Test
    void registerUser_WhenValidRequest_ThenSuccess() {
        RegistrationRequest request = initializeRegistrationRequest();
        User user = TestUserBuilder.createTestUser();
        User savedUser = TestUserBuilder.createTestUser();
        Account account = TestAccountBuilder.createTestAccount(1, BigDecimal.ZERO, savedUser);

        RegistrationResponse expectedResponse = new RegistrationResponse(
                user.getFirstname(),
                user.getLastname(),
                user.getEmail().toString(),
                user.getUsername()
        );

        when(registrationMapper.toUser(request)).thenReturn(user);
        when(userService.createUser(user)).thenReturn(savedUser);
        when(accountService.createAccount(savedUser.getId())).thenReturn(account);
        when(registrationMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        RegistrationResponse result = registrationService.registerUser(request);

        assertNotNull(result);
        assertEquals(savedUser.getEmail().toString(), result.email());
        assertEquals(savedUser.getFirstname(), result.firstname());
        assertEquals(savedUser.getLastname(), result.lastname());
        assertEquals(savedUser.getUsername(), result.username());

        verify(registrationMapper).toUser(request);
        verify(userService).createUser(user);
        verify(accountService).createAccount(savedUser.getId());
        verify(bonusGrantingService).grantWelcomeBonus(account.getIban(), defaultWelcomeBonusAmount);
        verify(emailService).sendWelcomeEmail(savedUser.getEmail().toString(), savedUser.getFullName(), savedUser.getUsername());
    }

    @Test
    void registerUser_WhenEmailAlreadyExists_ThenThrowException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstname("Jane");
        request.setLastname("Smith");
        request.setEmail("jane.smith@example.com");
        request.setPassword("StrongP@ss123");
        request.setConfirmPassword("StrongP@ss123");
        User user = TestUserBuilder.createTestUser();

        when(registrationMapper.toUser(request)).thenReturn(user);
        when(userService.createUser(user)).thenThrow(new IllegalArgumentException("Email already exists"));

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> registrationService.registerUser(request));

        assertEquals("Email already exists", exception.getMessage());

        verify(registrationMapper).toUser(request);
        verify(userService).createUser(user);
        verifyNoInteractions(accountService, bonusGrantingService, emailService);
    }

    @Test
    void registerUser_WhenBonusGrantingFails_ThenRollbackProcess() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstname("Alice");
        request.setLastname("Johnson");
        request.setEmail("alice.johnson@example.com");
        request.setPassword("StrongP@ss123");
        request.setConfirmPassword("StrongP@ss123");

        User user = TestUserBuilder.createTestUser();
        User savedUser = TestUserBuilder.createTestUser();
        Account account = TestAccountBuilder.createTestAccount(1, BigDecimal.ZERO, savedUser);

        when(registrationMapper.toUser(request)).thenReturn(user);
        when(userService.createUser(user)).thenReturn(savedUser);
        when(accountService.createAccount(savedUser.getId())).thenReturn(account);
        doThrow(new RuntimeException("Bonus granting failed")).when(bonusGrantingService)
                .grantWelcomeBonus(account.getIban(), defaultWelcomeBonusAmount);

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> registrationService.registerUser(request));

        assertEquals("Bonus granting failed", exception.getMessage());

        verify(registrationMapper).toUser(request);
        verify(userService).createUser(user);
        verify(accountService).createAccount(savedUser.getId());
        verify(bonusGrantingService).grantWelcomeBonus(account.getIban(), defaultWelcomeBonusAmount);
        verifyNoInteractions(emailService);
    }
}