package info.mackiewicz.bankapp.system.error.handling.service;

import info.mackiewicz.bankapp.system.error.handling.dto.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ValidationErrorProcessor Tests")
class ValidationErrorProcessorTest {

    private ValidationErrorProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ValidationErrorProcessor();
    }

    @Nested
    @DisplayName("Method Argument Validation Tests")
    class MethodArgumentValidationTests {

        @Test
        @DisplayName("Should convert MethodArgumentNotValidException to ValidationErrors")
        void shouldConvertMethodArgumentNotValidException() {
            // Arrange
            Object target = new Object();
            BindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
            bindingResult.addError(new FieldError("target", "username", "test123",
                    false, null, null, "Username must be valid"));
            bindingResult.addError(new FieldError("target", "email", "invalid-email",
                    false, null, null, "Must be a valid email"));

            MethodParameter methodParameter = mock(MethodParameter.class);
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

            // Act
            List<ValidationError> errors = processor.extractValidationErrors(ex);

            // Assert
            assertThat(errors).hasSize(2);
            assertThat(errors).anySatisfy(error -> {
                assertThat(error.getField()).isEqualTo("username");
                assertThat(error.getMessage()).isEqualTo("Username must be valid");
                assertThat(error.getRejectedValue()).isEqualTo("test123");
            });
            assertThat(errors).anySatisfy(error -> {
                assertThat(error.getField()).isEqualTo("email");
                assertThat(error.getMessage()).isEqualTo("Must be a valid email");
                assertThat(error.getRejectedValue()).isEqualTo("invalid-email");
            });
        }

        @Test
        @DisplayName("Should handle null rejected values in MethodArgumentNotValidException")
        void shouldHandleNullRejectedValues() {
            // Arrange
            Object target = new Object();
            BindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
            bindingResult.addError(new FieldError("target", "username", null,
                    false, null, null, "Username is required"));

            MethodParameter methodParameter = mock(MethodParameter.class);
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

            // Act
            List<ValidationError> errors = processor.extractValidationErrors(ex);

            // Assert
            assertThat(errors).hasSize(1);
            ValidationError error = errors.get(0);
            assertThat(error.getField()).isEqualTo("username");
            assertThat(error.getMessage()).isEqualTo("Username is required");
            assertThat(error.getRejectedValue()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Constraint Violation Tests")
    class ConstraintViolationTests {

        @Test
        @DisplayName("Should convert ConstraintViolationException to ValidationErrors")
        void shouldConvertConstraintViolationException() {
            // Arrange
            ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
            ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);

            Path path1 = mock(Path.class);
            Path path2 = mock(Path.class);

            when(path1.toString()).thenReturn("firstName");
            when(path2.toString()).thenReturn("phoneNumber");

            when(violation1.getPropertyPath()).thenReturn(path1);
            when(violation1.getMessage()).thenReturn("First name cannot be empty");
            when(violation1.getInvalidValue()).thenReturn("");

            when(violation2.getPropertyPath()).thenReturn(path2);
            when(violation2.getMessage()).thenReturn("Invalid phone number format");
            when(violation2.getInvalidValue()).thenReturn("123-456");

            ConstraintViolationException ex = new ConstraintViolationException("Validation failed", Set.of(violation1, violation2));

            // Act
            List<ValidationError> errors = processor.extractValidationErrors(ex);

            // Assert
            assertThat(errors).hasSize(2);
            assertThat(errors).anySatisfy(error -> {
                assertThat(error.getField()).isEqualTo("firstName");
                assertThat(error.getMessage()).isEqualTo("First name cannot be empty");
                assertThat(error.getRejectedValue()).isEqualTo("");
            });
            assertThat(errors).anySatisfy(error -> {
                assertThat(error.getField()).isEqualTo("phoneNumber");
                assertThat(error.getMessage()).isEqualTo("Invalid phone number format");
                assertThat(error.getRejectedValue()).isEqualTo("123-456");
            });
        }

        @Test
        @DisplayName("Should handle null invalid values in ConstraintViolationException")
        void shouldHandleNullInvalidValues() {
            // Arrange
            ConstraintViolation<?> violation = mock(ConstraintViolation.class);
            Path path = mock(Path.class);

            when(path.toString()).thenReturn("address");
            when(violation.getPropertyPath()).thenReturn(path);
            when(violation.getMessage()).thenReturn("Address is required");
            when(violation.getInvalidValue()).thenReturn(null);

            ConstraintViolationException ex = new ConstraintViolationException("Validation failed", Set.of(violation));

            // Act
            List<ValidationError> errors = processor.extractValidationErrors(ex);

            // Assert
            assertThat(errors).hasSize(1);
            ValidationError error = errors.get(0);
            assertThat(error.getField()).isEqualTo("address");
            assertThat(error.getMessage()).isEqualTo("Address is required");
            assertThat(error.getRejectedValue()).isEmpty();
        }
    }
}