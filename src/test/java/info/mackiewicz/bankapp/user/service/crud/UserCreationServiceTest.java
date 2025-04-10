package info.mackiewicz.bankapp.user.service.crud;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import info.mackiewicz.bankapp.user.service.util.UserValidationService;
import info.mackiewicz.bankapp.user.service.util.UsernameGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UserCreationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UsernameGeneratorService usernameGeneratorService;
    
    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private UserCreationService userCreationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldGenerateUsernameEncodePasswordAndSaveUser() {
        // Given
        User inputUser = new User();
        inputUser.setPassword("rawPassword");
        inputUser.setFirstname("Jan");
        inputUser.setLastname("Kowalski");
        inputUser.setEmail(new EmailAddress("jan.kowalski@example.com"));
    
        User userWithGeneratedUsername = new User();
        userWithGeneratedUsername.setPassword("rawPassword");
        userWithGeneratedUsername.setFirstname("Jan");
        userWithGeneratedUsername.setLastname("Kowalski");
        userWithGeneratedUsername.setEmail(new EmailAddress("jan.kowalski@example.com"));
        userWithGeneratedUsername.setUsername("generatedUsername");

        User userWithEncodedPassword = new User();
        userWithEncodedPassword.setPassword("encodedPassword");
        userWithEncodedPassword.setUsername("generatedUsername");
        userWithEncodedPassword.setFirstname("Jan");
        userWithEncodedPassword.setLastname("Kowalski");
        userWithEncodedPassword.setEmail(new EmailAddress("jan.kowalski@example.com"));
    
        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setPassword("encodedPassword");
        savedUser.setUsername("generatedUsername");
        savedUser.setFirstname("Jan");
        savedUser.setLastname("Kowalski");
        savedUser.setEmail(new EmailAddress("jan.kowalski@example.com"));
    
        // When
        doNothing().when(userValidationService).validateNewUser(inputUser);
        when(usernameGeneratorService.generateUsername(inputUser.getFirstname(), inputUser.getLastname(), inputUser.getEmail().toString())).thenReturn("generatedUsername");
        when(passwordService.ensurePasswordEncoded(userWithGeneratedUsername)).thenReturn(userWithEncodedPassword);
        when(userRepository.save(userWithEncodedPassword)).thenReturn(savedUser);

        User result = userCreationService.createUser(inputUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("generatedUsername", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());

        verify(usernameGeneratorService).generateUsername(inputUser.getFirstname(), inputUser.getLastname(), inputUser.getEmail().toString());
        verify(passwordService).ensurePasswordEncoded(any(User.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_whenUserAlreadyHasUsername_shouldNotGenerateNewUsername() {
        // Given
        User inputUser = new User();
        inputUser.setUsername("existingUsername");
        inputUser.setPassword("rawPassword");

        User userWithEncodedPassword = new User();
        userWithEncodedPassword.setUsername("existingUsername");
        userWithEncodedPassword.setPassword("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername("existingUsername");
        savedUser.setPassword("encodedPassword");

        // When
        doNothing().when(userValidationService).validateNewUser(inputUser);
        when(passwordService.ensurePasswordEncoded(inputUser)).thenReturn(userWithEncodedPassword);
        when(userRepository.save(userWithEncodedPassword)).thenReturn(savedUser);

        User result = userCreationService.createUser(inputUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("existingUsername", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());

        verify(usernameGeneratorService, never()).generateUsername(anyString(), anyString(), anyString());
        verify(passwordService).ensurePasswordEncoded(inputUser);
        verify(userRepository).save(userWithEncodedPassword);
    }
}