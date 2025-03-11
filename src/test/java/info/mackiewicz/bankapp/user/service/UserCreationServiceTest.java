package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.repository.UserRepository;
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

        User userWithGeneratedUsername = new User();
        userWithGeneratedUsername.setPassword("rawPassword");
        userWithGeneratedUsername.setUsername("generatedUsername");

        User userWithEncodedPassword = new User();
        userWithEncodedPassword.setPassword("encodedPassword");
        userWithEncodedPassword.setUsername("generatedUsername");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setPassword("encodedPassword");
        savedUser.setUsername("generatedUsername");

        // When
        when(usernameGeneratorService.generateUsername(inputUser)).thenReturn(userWithGeneratedUsername);
        when(passwordService.ensurePasswordEncoded(userWithGeneratedUsername)).thenReturn(userWithEncodedPassword);
        when(userRepository.save(userWithEncodedPassword)).thenReturn(savedUser);

        User result = userCreationService.createUser(inputUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("generatedUsername", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());

        verify(usernameGeneratorService).generateUsername(inputUser);
        verify(passwordService).ensurePasswordEncoded(userWithGeneratedUsername);
        verify(userRepository).save(userWithEncodedPassword);
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
        when(passwordService.ensurePasswordEncoded(inputUser)).thenReturn(userWithEncodedPassword);
        when(userRepository.save(userWithEncodedPassword)).thenReturn(savedUser);

        User result = userCreationService.createUser(inputUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("existingUsername", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());

        verify(usernameGeneratorService, never()).generateUsername(any());
        verify(passwordService).ensurePasswordEncoded(inputUser);
        verify(userRepository).save(userWithEncodedPassword);
    }
}