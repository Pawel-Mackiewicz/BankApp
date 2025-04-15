package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.system.registration.dto.DemoRegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.system.registration.dto.demo.RegistrationRequestFactory;
import info.mackiewicz.bankapp.system.registration.exception.DemoRegistrationException;
import info.mackiewicz.bankapp.user.exception.DuplicatedEmailException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DemoRegistrationServiceTest {
    private static final String VALID_EMAIL = "john.smith@example.com";
    private static final String DUPLICATE_EMAIL = "duplicate@example.com";
    private static final String INVALID_EMAIL = "invalid@example.com";
    private static final String TEST_EMAIL = "test@example.com";

    private static final String STRONG_PASSWORD = "StrongP@ss123";
    private static final String WEAK_PASSWORD = "weak";

    private static final String TEST_FIRSTNAME = "John";
    private static final String TEST_LASTNAME = "Smith";
    private static final String TEST_USERNAME = "john.smith12345";

    @Mock
    private RegistrationService registrationService;

    @Mock
    private RegistrationRequestFactory requestFactory;

    @InjectMocks
    private DemoRegistrationService demoRegistrationService;

    public DemoRegistrationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidDemoRequest_whenRegisterDemoUser_thenReturnRegistrationResponse() {
        // Prepare demo registration request with valid credentials
        DemoRegistrationRequest demoRequest = new DemoRegistrationRequest();
        demoRequest.setEmail(VALID_EMAIL);
        demoRequest.setPassword(STRONG_PASSWORD);
        demoRequest.setConfirmPassword(STRONG_PASSWORD);

        // Mock registration request and prepare expected response
        RegistrationRequest registrationRequest = mock(RegistrationRequest.class);
        RegistrationResponse registrationResponse = RegistrationResponse.builder()
                .withFirstname(TEST_FIRSTNAME)
                .withLastname(TEST_LASTNAME)
                .withEmail(VALID_EMAIL)
                .withUsername(TEST_USERNAME)
                .build();

        // Configure mocks behavior
        when(requestFactory.createDemoRegistrationRequest(demoRequest.getEmail(), demoRequest.getPassword())).thenReturn(registrationRequest);
        when(registrationService.registerUser(any(RegistrationRequest.class))).thenReturn(registrationResponse);

        // Execute registration
        RegistrationResponse result = demoRegistrationService.registerDemoUser(demoRequest);

        // Verify response data
        assertEquals(TEST_FIRSTNAME, result.firstname());
        assertEquals(TEST_LASTNAME, result.lastname());
        assertEquals(VALID_EMAIL, result.email());
        assertEquals(TEST_USERNAME, result.username());

        // Verify mock interactions
        verify(requestFactory, times(1)).createDemoRegistrationRequest(demoRequest.getEmail(), demoRequest.getPassword());
        verify(registrationService, times(1)).registerUser(registrationRequest);
    }

    @Test
    void givenDuplicateEmail_whenRegisterDemoUser_thenThrowDuplicatedEmailException() {
        // Prepare demo registration request with duplicate email
        DemoRegistrationRequest demoRequest = new DemoRegistrationRequest();
        demoRequest.setEmail(DUPLICATE_EMAIL);
        demoRequest.setPassword(STRONG_PASSWORD);
        demoRequest.setConfirmPassword(STRONG_PASSWORD);

        // Mock registration request
        RegistrationRequest registrationRequest = mock(RegistrationRequest.class);

        // Configure mocks to simulate duplicate email scenario
        when(requestFactory.createDemoRegistrationRequest(demoRequest.getEmail(), demoRequest.getPassword())).thenReturn(registrationRequest);
        doThrow(DuplicatedEmailException.class).when(registrationService).registerUser(any(RegistrationRequest.class));

        // Verify exception is thrown
        assertThrows(DuplicatedEmailException.class, () -> demoRegistrationService.registerDemoUser(demoRequest));

        // Verify mock interactions
        verify(requestFactory, times(1)).createDemoRegistrationRequest(demoRequest.getEmail(), demoRequest.getPassword());
        verify(registrationService, times(1)).registerUser(registrationRequest);
    }

    @Test
    void givenUnexpectedException_whenRegisterDemoUser_thenThrowDemoRegistrationException() {
        // Prepare demo registration request
        DemoRegistrationRequest demoRequest = new DemoRegistrationRequest();
        demoRequest.setEmail(INVALID_EMAIL);
        demoRequest.setPassword(STRONG_PASSWORD);
        demoRequest.setConfirmPassword(STRONG_PASSWORD);

        // Mock registration request
        RegistrationRequest registrationRequest = mock(RegistrationRequest.class);

        // Configure mocks to simulate unexpected runtime exception
        when(requestFactory.createDemoRegistrationRequest(demoRequest.getEmail(), demoRequest.getPassword())).thenReturn(registrationRequest);
        doThrow(RuntimeException.class).when(registrationService).registerUser(any(RegistrationRequest.class));

        // Verify DemoRegistrationException is thrown
        assertThrows(DemoRegistrationException.class, () -> demoRegistrationService.registerDemoUser(demoRequest));

        // Verify mock interactions
        verify(requestFactory, times(1)).createDemoRegistrationRequest(demoRequest.getEmail(), demoRequest.getPassword());
        verify(registrationService, times(1)).registerUser(registrationRequest);
    }

    @Test
    void givenInvalidPassword_whenRegisterDemoUser_thenThrowDemoRegistrationException() {
        // Prepare demo registration request with weak password
        DemoRegistrationRequest demoRequest = new DemoRegistrationRequest();
        demoRequest.setEmail(TEST_EMAIL);
        demoRequest.setPassword(WEAK_PASSWORD);
        demoRequest.setConfirmPassword(WEAK_PASSWORD);

        // Configure mock to throw exception for invalid password
        when(requestFactory.createDemoRegistrationRequest(demoRequest.getEmail(), demoRequest.getPassword()))
                .thenThrow(DemoRegistrationException.class);

        // Verify DemoRegistrationException is thrown
        assertThrows(DemoRegistrationException.class, () -> demoRegistrationService.registerDemoUser(demoRequest));

        // Verify mock interactions
        verify(requestFactory, times(1)).createDemoRegistrationRequest(demoRequest.getEmail(), demoRequest.getPassword());
        verifyNoInteractions(registrationService);
    }
}