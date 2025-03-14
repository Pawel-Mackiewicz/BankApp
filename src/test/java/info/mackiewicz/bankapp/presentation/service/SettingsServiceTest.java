package info.mackiewicz.bankapp.presentation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.mackiewicz.bankapp.presentation.dashboard.dto.UserSettingsDTO;
import info.mackiewicz.bankapp.presentation.dashboard.service.SettingsService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import info.mackiewicz.bankapp.user.service.UserService;

class SettingsServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(SettingsServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SettingsService settingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSettingsByUserId() {
        logger.info("testGetSettingsByUserId: Starting test");
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail(new Email("test@example.com"));
        user.setPhoneNumber(new PhoneNumber("123456789"));

        when(userService.getUserById(userId)).thenReturn(user);

        UserSettingsDTO settings = settingsService.getUserSettings(userId);

        assertNotNull(settings);
        assertEquals("testuser", settings.getUsername());
        assertEquals("test@example.com", settings.getEmail());
        assertEquals("+48123456789", settings.getPhoneNumber());
        assertEquals("Test", settings.getFirstname());
        assertEquals("User", settings.getLastname());
        
        logger.info("testGetSettingsByUserId: Test passed");
    }
}