package info.mackiewicz.bankapp.shared.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import info.mackiewicz.bankapp.security.exception.ExpiredTokenException;
import info.mackiewicz.bankapp.security.exception.TokenNotFoundException;
import info.mackiewicz.bankapp.security.exception.TooManyPasswordResetAttemptsException;
import info.mackiewicz.bankapp.security.exception.UsedTokenException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;
import info.mackiewicz.bankapp.shared.infrastructure.logging.ApiErrorLogger;
import info.mackiewicz.bankapp.shared.web.dto.BaseApiError;
import info.mackiewicz.bankapp.shared.web.dto.ValidationApiError;
import info.mackiewicz.bankapp.shared.web.dto.ValidationError;
import info.mackiewicz.bankapp.shared.web.error.mapping.ApiExceptionToErrorMapper;
import info.mackiewicz.bankapp.shared.web.error.validation.ValidationErrorProcessor;
import info.mackiewicz.bankapp.shared.web.util.RequestUriHandler;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

    private ApiExceptionHandler exceptionHandler;

    @Mock
    private RequestUriHandler uriHandler;

    @Mock
    private ApiErrorLogger errorLogger;

    @Mock
    private ApiExceptionToErrorMapper exceptionMapper;

    @Mock
    private ValidationErrorProcessor validationErrorProcessor;

    @Mock
    private WebRequest webRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private ConstraintViolationException constraintViolationException;

    @Mock
    private BindingResult bindingResult;

    private static final String TEST_PATH = "/api/security/password-reset";
    private static final String TEST_ERROR_MESSAGE = "Test error message";

    @BeforeEach
    void setUp() {
        exceptionHandler = new ApiExceptionHandler(
                uriHandler,
                errorLogger,
                exceptionMapper,
                validationErrorProcessor);

        when(uriHandler.getRequestURI(any(WebRequest.class))).thenReturn(TEST_PATH);
    }

    @Nested
    @DisplayName("Validation Exception Tests")
    class ValidationExceptionTests {

        @Test
        @DisplayName("Should handle MethodArgumentNotValidException")
        void shouldHandleMethodArgumentNotValidException() {
            // Arrange
            List<ValidationError> mockErrors = List.of(
                new ValidationError("field1", "error1", "value1"),
                new ValidationError("field2", "error2", "value2")
            );
            when(validationErrorProcessor.extractValidationErrors(methodArgumentNotValidException))
                .thenReturn(mockErrors);

            // Act
            ResponseEntity<ValidationApiError> response = exceptionHandler
                .handleMethodArgumentNotValidException(methodArgumentNotValidException, webRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody())
                .isNotNull()
                .satisfies(body -> {
                    assertThat(body.getPath()).isEqualTo(TEST_PATH);
                    assertThat(body.getErrors()).isEqualTo(mockErrors);
                });

            verify(errorLogger).logError(eq(ErrorCode.VALIDATION_ERROR), 
                eq(methodArgumentNotValidException), eq(TEST_PATH));
        }

        @Test
        @DisplayName("Should handle ConstraintViolationException")
        void shouldHandleConstraintViolationException() {
            // Arrange
            List<ValidationError> mockErrors = List.of(
                new ValidationError("constraint1", "error1", "value1"),
                new ValidationError("constraint2", "error2", "value2")
            );
            when(validationErrorProcessor.extractValidationErrors(constraintViolationException))
                .thenReturn(mockErrors);

            // Act
            ResponseEntity<ValidationApiError> response = exceptionHandler
                .handleConstraintViolationException(constraintViolationException, webRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody())
                .isNotNull()
                .satisfies(body -> {
                    assertThat(body.getPath()).isEqualTo(TEST_PATH);
                    assertThat(body.getErrors()).isEqualTo(mockErrors);
                });

            verify(errorLogger).logError(eq(ErrorCode.VALIDATION_ERROR), 
                eq(constraintViolationException), eq(TEST_PATH));
        }
    }

    @Nested
    @DisplayName("Password Reset Exception Tests")
    class PasswordResetExceptionTests {

        @Test
        @DisplayName("Should handle TokenNotFoundException")
        void shouldHandleTokenNotFoundException() {
            // Arrange
            TokenNotFoundException ex = new TokenNotFoundException(TEST_ERROR_MESSAGE);
            when(exceptionMapper.map(ex)).thenReturn(ErrorCode.TOKEN_NOT_FOUND);

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(ex, webRequest);

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

            verify(errorLogger).logError(eq(ErrorCode.TOKEN_NOT_FOUND), eq(ex), eq(TEST_PATH));
        }

        @Test
        @DisplayName("Should handle ExpiredTokenException")
        void shouldHandleExpiredTokenException() {
            // Arrange
            ExpiredTokenException ex = new ExpiredTokenException(TEST_ERROR_MESSAGE);
            when(exceptionMapper.map(ex)).thenReturn(ErrorCode.TOKEN_EXPIRED);

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(ex, webRequest);

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

            verify(errorLogger).logError(eq(ErrorCode.TOKEN_EXPIRED), eq(ex), eq(TEST_PATH));
        }

        @Test
        @DisplayName("Should handle TooManyPasswordResetAttemptsException")
        void shouldHandleTooManyPasswordResetAttemptsException() {
            // Arrange
            TooManyPasswordResetAttemptsException ex = new TooManyPasswordResetAttemptsException(TEST_ERROR_MESSAGE);
            when(exceptionMapper.map(ex)).thenReturn(ErrorCode.TOO_MANY_PASSWORD_RESET_ATTEMPTS);

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(ex, webRequest);

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

            verify(errorLogger).logError(eq(ErrorCode.TOO_MANY_PASSWORD_RESET_ATTEMPTS), eq(ex), eq(TEST_PATH));
        }

        @Test
        @DisplayName("Should handle UsedTokenException")
        void shouldHandleUsedTokenException() {
            // Arrange
            UsedTokenException ex = new UsedTokenException(TEST_ERROR_MESSAGE);
            when(exceptionMapper.map(ex)).thenReturn(ErrorCode.TOKEN_USED);

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(ex, webRequest);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GONE);
            assertThat(response.getBody())
                .isNotNull()
                .satisfies(body -> {
                    assertThat(body.getTitle()).isEqualTo(ErrorCode.TOKEN_USED.name());
                    assertThat(body.getPath()).isEqualTo(TEST_PATH);
                    assertThat(body.getMessage()).isEqualTo(ErrorCode.TOKEN_USED.getMessage());
                });

            verify(errorLogger).logError(eq(ErrorCode.TOKEN_USED), eq(ex), eq(TEST_PATH));
        }
    }

    @Nested
    @DisplayName("Other Exception Tests")
    class OtherExceptionTests {

        @Test
        @DisplayName("Should handle UserNotFoundException")
        void shouldHandleUserNotFoundException() {
            // Arrange
            UserNotFoundException ex = new UserNotFoundException(TEST_ERROR_MESSAGE);
            when(exceptionMapper.map(ex)).thenReturn(ErrorCode.USER_NOT_FOUND);

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(ex, webRequest);

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

            verify(errorLogger).logError(eq(ErrorCode.USER_NOT_FOUND), eq(ex), eq(TEST_PATH));
        }

        @Test
        @DisplayName("Should handle BadCredentialsException")
        void shouldHandleBadCredentialsException() {
            // Arrange
            BadCredentialsException ex = new BadCredentialsException(TEST_ERROR_MESSAGE);
            when(exceptionMapper.map(ex)).thenReturn(ErrorCode.INVALID_CREDENTIALS);

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(ex, webRequest);

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

            verify(errorLogger).logError(eq(ErrorCode.INVALID_CREDENTIALS), eq(ex), eq(TEST_PATH));
        }

        @Test
        @DisplayName("Should handle unknown exceptions with internal error")
        void shouldHandleUnknownExceptionWithInternalError() {
            // Arrange
            RuntimeException ex = new RuntimeException(TEST_ERROR_MESSAGE);
            when(exceptionMapper.map(ex)).thenReturn(ErrorCode.INTERNAL_ERROR);

            // Act
            ResponseEntity<BaseApiError> response = exceptionHandler.handleException(ex, webRequest);

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

            verify(errorLogger).logError(eq(ErrorCode.INTERNAL_ERROR), eq(ex), eq(TEST_PATH));
        }
    }
}
