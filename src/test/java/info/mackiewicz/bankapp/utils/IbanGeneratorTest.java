package info.mackiewicz.bankapp.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import info.mackiewicz.bankapp.account.util.IbanGenerator;
import info.mackiewicz.bankapp.shared.util.IbanValidator;

class IbanGeneratorTest {

    @ParameterizedTest
    @CsvSource({
        "1, 1, 1",
        "123, 17, 2091",
        "456, 17, 7752"
    })
    void shouldCorrectlyEncodeUserId(int userId, int multiplier, int expected) {
        // Given
        int encodedId = userId * multiplier;
    
        // Then
        assertEquals(expected, encodedId);
    }
    
    @Test
    void shouldGenerateValidIban() {
        // Given
        Integer userId = 123;
        Integer accountCounter = 1;
    
        // When
        String iban = IbanGenerator.generateIban(userId, accountCounter);
    
        // Then
        assertTrue(IbanValidator.isValid(iban));
    }
    
    @Test
    void shouldFormatIbanWithSpaces() {
        // Given
        String unformattedIban = "PL78485100209100000000000001";
    
        // When
        String formattedIban = IbanGenerator.formatIban(unformattedIban);
    
        // Then
        assertEquals("PL78 4851 0020 9100 0000 0000 0001", formattedIban);
    }
    
    @Test
    void generatedIbanShouldHaveCorrectLength() {
        // Given
        Integer userId = 123;
        Integer accountCounter = 1;
    
        // When
        String iban = IbanGenerator.generateIban(userId, accountCounter);
    
        // Then
        assertEquals(28, iban.length());
    }
    
    @Test
    void shouldGenerateUniqueIbansForDifferentAccounts() {
        // Given
        String iban1 = IbanGenerator.generateIban(123, 1);
        String iban2 = IbanGenerator.generateIban(123, 2);
        String iban3 = IbanGenerator.generateIban(456, 1);
    
        // Then
        assertNotEquals(iban1, iban2);
        assertNotEquals(iban1, iban3);
        assertNotEquals(iban2, iban3);
    }
    }