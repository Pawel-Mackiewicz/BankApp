package info.mackiewicz.bankapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService(passwordEncoder);
    }

    @Test
    void ensurePasswordEncoded_WhenPasswordNotEncoded_ShouldEncodePassword() {
        // given
        User user = new User();
        String rawPassword = "testPassword123";
        String encodedPassword = "$2a$10$encoded";
        user.setPassword(rawPassword);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // when
        User result = passwordService.ensurePasswordEncoded(user);

        // then
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void ensurePasswordEncoded_WhenPasswordAlreadyEncoded_ShouldNotReEncode() {
        // given
        User user = new User();
        String encodedPassword = "$2a$10$alreadyEncodedPassword";
        user.setPassword(encodedPassword);

        // when
        User result = passwordService.ensurePasswordEncoded(user);

        // then
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void encodePassword_ShouldDelegateToPasswordEncoder() {
        // given
        String rawPassword = "testPassword123";
        String encodedPassword = "$2a$10$encoded";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // when
        String result = passwordService.encodePassword(rawPassword);

        // then
        assertThat(result).isEqualTo(encodedPassword);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void verifyPassword_WhenPasswordsMatch_ShouldReturnTrue() {
        // given
        String rawPassword = "testPassword123";
        String encodedPassword = "$2a$10$encoded";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // when
        boolean result = passwordService.verifyPassword(rawPassword, encodedPassword);

        // then
        assertThat(result).isTrue();
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void verifyPassword_WhenPasswordsDoNotMatch_ShouldReturnFalse() {
        // given
        String rawPassword = "testPassword123";
        String encodedPassword = "$2a$10$encoded";
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // when
        boolean result = passwordService.verifyPassword(rawPassword, encodedPassword);

        // then
        assertThat(result).isFalse();
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void ensurePasswordEncoded_ShouldRecognizeMultipleBCryptFormats() {
        // given
        String[] bcryptFormats = {
            "$2a$10$encoded",
            "$2b$10$encoded",
            "$2y$10$encoded"
        };

        for (String encodedPassword : bcryptFormats) {
            User user = new User();
            user.setPassword(encodedPassword);

            // when
            User result = passwordService.ensurePasswordEncoded(user);

            // then
            assertThat(result.getPassword()).isEqualTo(encodedPassword);
            verify(passwordEncoder, never()).encode(encodedPassword);
        }
    }
}