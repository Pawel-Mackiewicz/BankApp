package info.mackiewicz.bankapp.core.user.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AgeRangeValidatorTest {

    private AgeRangeValidatior validator;

    @BeforeEach
    void setUp() {
        validator = new AgeRangeValidatior();
        validator.initialize(null); // No configuration needed
    }

    @Test
    @DisplayName("Should return true for 18 years old")
    void shouldValidateMinimumAge() {
        // Arrange
        LocalDate eighteenYearsOld = LocalDate.now().minusYears(18);

        // Act
        boolean result = validator.isValid(eighteenYearsOld, null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return true for 120 years old")
    void shouldValidateMaximumAge() {
        // Arrange
        LocalDate maxAge = LocalDate.now().minusYears(120);

        // Act
        boolean result = validator.isValid(maxAge, null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false for null birth date")
    void shouldReturnFalseForNullDate() {
        // Act
        boolean result = validator.isValid(null, null);

        // Assert
        assertThat(result).isFalse();
    }

    @ParameterizedTest(name = "Age {0} should be invalid")
    @ValueSource(ints = {1, 10, 17})
    @DisplayName("Should return false for ages under 18")
    void shouldReturnFalseForUnderageUsers(int age) {
        // Arrange
        LocalDate underage = LocalDate.now().minusYears(age);

        // Act
        boolean result = validator.isValid(underage, null);

        // Assert
        assertThat(result).isFalse();
    }

    @ParameterizedTest(name = "Age {0} should be invalid")
    @ValueSource(ints = {121, 150, 200})
    @DisplayName("Should return false for ages over 120")
    void shouldReturnFalseForOverAgeUsers(int age) {
        // Arrange
        LocalDate overAge = LocalDate.now().minusYears(age);

        // Act
        boolean result = validator.isValid(overAge, null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true for typical adult age")
    void shouldReturnTrueForTypicalAdultAge() {
        // Arrange
        LocalDate typicalAge = LocalDate.now().minusYears(35);

        // Act
        boolean result = validator.isValid(typicalAge, null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return true for edge case: exact 18th birthday")
    void shouldReturnTrueForExact18thBirthday() {
        // Arrange
        LocalDate exactlyEighteen = LocalDate.now().minusYears(18);

        // Act
        boolean result = validator.isValid(exactlyEighteen, null);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return true for edge case: exact 120th birthday")
    void shouldReturnTrueForExact120thBirthday() {
        // Arrange
        LocalDate exactly120 = LocalDate.now().minusYears(120);

        // Act
        boolean result = validator.isValid(exactly120, null);

        // Assert
        assertThat(result).isTrue();
    }
}