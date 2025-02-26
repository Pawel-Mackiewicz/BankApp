package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userService.getUserById(userId)).thenReturn(user);

        User settings = settingsService.getUserSettings(userId);

        assertNotNull(settings);
        assertEquals("testuser", settings.getUsername());
        logger.info("testGetSettingsByUserId: Test passed");
    }

    @Test
    void testUpdateSettings() {
        logger.info("testUpdateSettings: Starting test");
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userService.updateUser(user)).thenReturn(user);
        when(userService.getUserById(userId)).thenReturn(user);

        User updatedSettings = userService.updateUser(user);

        assertNotNull(updatedSettings);
        assertEquals("testuser", updatedSettings.getUsername());
        logger.info("testUpdateSettings: Test passed");
    }
}