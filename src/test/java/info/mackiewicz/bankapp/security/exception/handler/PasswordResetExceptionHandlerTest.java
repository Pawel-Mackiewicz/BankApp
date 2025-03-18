package info.mackiewicz.bankapp.security.exception.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import info.mackiewicz.bankapp.security.exception.ExpiredTokenException;
import info.mackiewicz.bankapp.security.exception.PasswordChangeException;
import info.mackiewicz.bankapp.security.exception.TokenNotFoundException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.shared.dto.BaseApiError;
import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class PasswordResetExceptionHandlerTest {

    private PasswordResetExceptionHandler exceptionHandler;

    @Mock
    private RequestUriHandler uriHandler;

    @Mock
    private ApiErrorLogger errorLogger;

    @Mock
    private PasswordResetExceptionToErrorMapper exceptionMapper;

    @Mock
    private ValidationErrorProcessor validationErrorProcessor;

    @Mock
    private ServletWebRequest servletWebRequest;

    private static final String TEST_PATH = "/api/security/test";

    @BeforeEach
    void setUp() {
        exceptionHandler = new PasswordResetExceptionHandler(uriHandler, errorLogger, exceptionMapper, validationErrorProcessor);
        
        when(uriHandler.getRequestURI(any(WebRequest.class))).thenReturn(TEST_PATH);
        
        // Configure exception mapper to return appropriate error codes
        when(exceptionMapper.map(any(TokenNotFoundException.class))).thenReturn(ErrorCode.TOKEN_NOT_FOUND);
        when(exceptionMapper.map(any(ExpiredTokenException.class))).thenReturn(ErrorCode.TOKEN_EXPIRED);
        when(exceptionMapper.map(any(BadCredentialsException.class))).thenReturn(ErrorCode.INVALID_CREDENTIALS);
        when(exceptionMapper.map(any(TooManyPasswordResetAttemptsException.class))).thenReturn(ErrorCode.TOO_MANY_PASSWORD_RESET_ATTEMPTS);
        when(exceptionMapper.map(any(UserNotFoundException.class))).thenReturn(ErrorCode.USER_NOT_FOUND);
        when(exceptionMapper.map(any(PasswordChangeException.class))).thenReturn(ErrorCode.INTERNAL_ERROR);
        when(exceptionMapper.map(any(RuntimeException.class))).thenReturn(ErrorCode.INTERNAL_ERROR);
    }

    @Nested
    @DisplayName("Security Exception Tests")
    class SecurityExceptionTests {
        
        @Test
        @DisplayName("Should handle TokenNotFoundException")
        void shouldHandleTokenNotFoundException() {
            // Arrange
            TokenNotFoundException exception = new TokenNotFoundException("Token not found");

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(exception, servletWebRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody())
                .isNotNull()
                .satisfies(body -> {
                    assertThat(body.getTitle()).isEqualTo(ErrorCode.TOKEN_NOT_FOUND.name());
                    assertThat(body.getPath()).isEqualTo(TEST_PATH);
                    assertThat(body.getMessage()).isEqualTo(ErrorCode.TOKEN_NOT_FOUND.getMessage());
                });
        }

        @Test
        @DisplayName("Should handle ExpiredTokenException")
        void shouldHandleExpiredTokenException() {
            // Arrange
            ExpiredTokenException exception = new ExpiredTokenException("Token expired");

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(exception, servletWebRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GONE);
            assertThat(response.getBody())
                .isNotNull()
                .satisfies(body -> {
                    assertThat(body.getTitle()).isEqualTo(ErrorCode.TOKEN_EXPIRED.name());
                    assertThat(body.getPath()).isEqualTo(TEST_PATH);
                    assertThat(body.getMessage()).isEqualTo(ErrorCode.TOKEN_EXPIRED.getMessage());
                });
        }

        @Test
        @DisplayName("Should handle BadCredentialsException")
        void shouldHandleBadCredentialsException() {
            // Arrange
            BadCredentialsException exception = new BadCredentialsException("Invalid credentials");

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(exception, servletWebRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody())
                .isNotNull()
                .satisfies(body -> {
                    assertThat(body.getTitle()).isEqualTo(ErrorCode.INVALID_CREDENTIALS.name());
                    assertThat(body.getPath()).isEqualTo(TEST_PATH);
                    assertThat(body.getMessage()).isEqualTo(ErrorCode.INVALID_CREDENTIALS.getMessage());
                });
        }

        @Test
        @DisplayName("Should handle TooManyPasswordResetAttemptsException")
        void shouldHandleTooManyPasswordResetAttemptsException() {
            // Arrange
            TooManyPasswordResetAttemptsException exception = new TooManyPasswordResetAttemptsException("Too many attempts");

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(exception, servletWebRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
            assertThat(response.getBody())
                .isNotNull()
                .satisfies(body -> {
                    assertThat(body.getTitle()).isEqualTo(ErrorCode.TOO_MANY_PASSWORD_RESET_ATTEMPTS.name());
                    assertThat(body.getPath()).isEqualTo(TEST_PATH);
                    assertThat(body.getMessage()).isEqualTo(ErrorCode.TOO_MANY_PASSWORD_RESET_ATTEMPTS.getMessage());
                });
        }
    }

    @Nested
    @DisplayName("User Exception Tests")
    class UserExceptionTests {

        @Test
        @DisplayName("Should handle UserNotFoundException")
        void shouldHandleUserNotFoundException() {
            // Arrange
            UserNotFoundException exception = new UserNotFoundException("User not found");

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(exception, servletWebRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody())
                .isNotNull()
                .satisfies(body -> {
                    assertThat(body.getTitle()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
                    assertThat(body.getPath()).isEqualTo(TEST_PATH);
                    assertThat(body.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
                });
        }

        @Test
        @DisplayName("Should handle PasswordChangeException")
        void shouldHandlePasswordChangeException() {
            // Arrange
            PasswordChangeException exception = new PasswordChangeException("Password change failed");

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(exception, servletWebRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody())
                .isNotNull()
                .satisfies(body -> {
                    assertThat(body.getTitle()).isEqualTo(ErrorCode.INTERNAL_ERROR.name());
                    assertThat(body.getPath()).isEqualTo(TEST_PATH);
                    assertThat(body.getMessage()).isEqualTo(ErrorCode.INTERNAL_ERROR.getMessage());
                });
        }
    }

    @Nested
    @DisplayName("Generic Exception Tests")
    class GenericExceptionTests {

        @Test
        @DisplayName("Should handle unknown exceptions with internal error")
        void shouldHandleUnknownExceptionWithInternalError() {
            // Arrange
            Exception exception = new RuntimeException("Unknown error");

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(exception, servletWebRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody())
                .isNotNull()
                .satisfies(body -> {
                    assertThat(body.getTitle()).isEqualTo(ErrorCode.INTERNAL_ERROR.name());
                    assertThat(body.getPath()).isEqualTo(TEST_PATH);
                    assertThat(body.getMessage()).isEqualTo(ErrorCode.INTERNAL_ERROR.getMessage());
                });
        }
    }
}
