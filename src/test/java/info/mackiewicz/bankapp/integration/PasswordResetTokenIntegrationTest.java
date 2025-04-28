package info.mackiewicz.bankapp.integration;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.repository.UserRepository;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordChangeForm;
import info.mackiewicz.bankapp.system.recovery.password.exception.ExpiredTokenException;
import info.mackiewicz.bankapp.system.recovery.password.exception.UsedTokenException;
import info.mackiewicz.bankapp.system.recovery.password.service.PasswordResetService;
import info.mackiewicz.bankapp.system.recovery.password.service.PasswordResetTokenService;
import info.mackiewicz.bankapp.system.token.model.PasswordResetToken;
import info.mackiewicz.bankapp.system.token.repository.PasswordResetTokenRepository;
import info.mackiewicz.bankapp.system.token.service.TokenOperationsService;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PasswordResetTokenIntegrationTest {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private TokenOperationsService tokenHashingService;
    
    @MockitoSpyBean
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private EntityManager entityManager;
    
    // Mockujemy EmailService aby uniknąć faktycznego wysyłania e-maili
    @MockitoBean
    private EmailService emailService;

    private User testUser;
    private static final String TEST_PASSWORD = "oldPassword123!";
    private static final String NEW_PASSWORD = "newPassword456!";
    // Stały token testowy do użycia w testach
    private static final String TEST_PLAIN_TOKEN = "test-token-for-reset-password";

    @BeforeEach
    void setUp() {
        // Konfigurujemy mock EmailService, aby nie rzucał wyjątków
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
        doNothing().when(emailService).sendPasswordResetConfirmation(anyString(), anyString());
        
        // Create test user with dynamic ID instead of fixed ID=1
        testUser = TestUserBuilder.createRandomTestUserForIntegrationTests();
        testUser.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        testUser = userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void whenRequestingPasswordReset_shouldCreateValidToken() {
        // When
        passwordResetService.requestReset(testUser.getEmail().toString());
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(tokenRepository.findValidTokensByUserEmail(testUser.getEmail().toString(), LocalDateTime.now()))
                .hasSize(1)
                .first()
                .satisfies(token -> {
                    assertThat(token.getUserEmail()).isEqualTo(testUser.getEmail().toString());
                    assertThat(token.isUsed()).isFalse();
                    assertThat(token.getExpiresAt()).isAfter(LocalDateTime.now());
                });
    }

    @Test
    void whenResettingPassword_shouldUpdatePasswordAndConsumeToken() {
        // Given
        // Przygotuj własny token testowy - symuluj utworzenie tokena
        String hashedToken = tokenHashingService.hashToken(TEST_PLAIN_TOKEN);
        PasswordResetToken token = new PasswordResetToken(hashedToken, testUser.getEmail().toString(), testUser.getFullName());
        token = tokenRepository.save(token);
        
        // Skonfiguruj zwracanie tego tokena przez getValidatedToken
        when(passwordResetTokenService.getValidatedToken(TEST_PLAIN_TOKEN)).thenReturn(token);
        
        String oldPasswordHash = testUser.getPassword();

        PasswordChangeForm resetDTO = new PasswordChangeForm();
        resetDTO.setToken(TEST_PLAIN_TOKEN); // Użyj oryginalnego tokena, nie hasha
        resetDTO.setPassword(NEW_PASSWORD);
        resetDTO.setConfirmPassword(NEW_PASSWORD);

        // When
        passwordResetService.completeReset(resetDTO);
        entityManager.flush();
        entityManager.clear();

        // Then
        User updatedUser = userRepository.findByEmail(testUser.getEmail()).orElseThrow();
        entityManager.refresh(updatedUser);
        PasswordResetToken updatedToken = tokenRepository.findByTokenHash(hashedToken).orElseThrow();
        entityManager.refresh(updatedToken);

        assertThat(updatedUser.getPassword())
                .isNotEqualTo(oldPasswordHash)
                .satisfies(newHash -> assertThat(passwordEncoder.matches(NEW_PASSWORD, newHash)).isTrue());

        assertThat(updatedToken.isUsed()).isTrue();
    }

    @Test
    void whenUsingExpiredToken_shouldThrowException() {
        // Given
        // Przygotuj własny token testowy - symuluj utworzenie tokena
        String hashedToken = tokenHashingService.hashToken(TEST_PLAIN_TOKEN);
        PasswordResetToken token = new PasswordResetToken(hashedToken, testUser.getEmail().toString(), testUser.getFullName());
        
        // Make token expired
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        token = tokenRepository.save(token);
        entityManager.flush();
        entityManager.clear();

        PasswordChangeForm resetDTO = new PasswordChangeForm();
        resetDTO.setToken(TEST_PLAIN_TOKEN);
        resetDTO.setPassword(NEW_PASSWORD);
        resetDTO.setConfirmPassword(NEW_PASSWORD);

        // Then
        assertThatThrownBy(() -> passwordResetService.completeReset(resetDTO))
                .isInstanceOf(ExpiredTokenException.class);

        User unchangedUser = userRepository.findByEmail(testUser.getEmail()).orElseThrow();
        entityManager.refresh(unchangedUser);
        assertThat(passwordEncoder.matches(TEST_PASSWORD, unchangedUser.getPassword())).isTrue();
    }

    @Test
    void whenUsingAlreadyUsedToken_shouldThrowException() {
        // Given
        // Przygotuj własny token testowy - symuluj utworzenie tokena
        String hashedToken = tokenHashingService.hashToken(TEST_PLAIN_TOKEN);
        PasswordResetToken token = new PasswordResetToken(hashedToken, testUser.getEmail().toString(), testUser.getFullName());
        token = tokenRepository.save(token);
        entityManager.flush();
        entityManager.clear();

        PasswordChangeForm resetDTO = new PasswordChangeForm();
        resetDTO.setToken(TEST_PLAIN_TOKEN);
        resetDTO.setPassword(NEW_PASSWORD);
        resetDTO.setConfirmPassword(NEW_PASSWORD);

        // First reset
        passwordResetService.completeReset(resetDTO);
        entityManager.flush();
        entityManager.clear();

        // Try to use same token again with different password
        PasswordChangeForm secondResetDTO = new PasswordChangeForm();
        secondResetDTO.setToken(TEST_PLAIN_TOKEN);
        secondResetDTO.setPassword("anotherPassword789!");
        secondResetDTO.setConfirmPassword("anotherPassword789!");

        // Then
        assertThatThrownBy(() -> passwordResetService.completeReset(secondResetDTO))
                .isInstanceOf(UsedTokenException.class);

        User user = userRepository.findByEmail(testUser.getEmail()).orElseThrow();
        entityManager.refresh(user);
        assertThat(passwordEncoder.matches(NEW_PASSWORD, user.getPassword())).isTrue();
    }
}