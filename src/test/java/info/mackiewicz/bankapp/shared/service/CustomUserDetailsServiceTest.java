package info.mackiewicz.bankapp.shared.service;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        when(userService.getUserByUsername(username)).thenReturn(user);

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        verify(userService, times(1)).getUserByUsername(username);
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        String username = "nonexistentuser";
        when(userService.getUserByUsername(username)).thenThrow(new UsernameNotFoundException("User not found"));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(username));
        verify(userService, times(1)).getUserByUsername(username);
    }
}