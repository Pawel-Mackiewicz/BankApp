package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.AdminUser;
import info.mackiewicz.bankapp.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminUserServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        String username = "admin";
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(username);
        when(adminUserRepository.findByUsername(username)).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails userDetails = adminUserService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        verify(adminUserRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        String username = "nonexistentuser";
        when(adminUserRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> adminUserService.loadUserByUsername(username));
        verify(adminUserRepository, times(1)).findByUsername(username);
    }
    
     @Test
    void loadUserByUsername_NullUsername_ThrowsUsernameNotFoundException() {
        // Arrange
        String username = null;
        when(adminUserRepository.findByUsername(username)).thenThrow(new IllegalArgumentException());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> adminUserService.loadUserByUsername(username));
    }
}