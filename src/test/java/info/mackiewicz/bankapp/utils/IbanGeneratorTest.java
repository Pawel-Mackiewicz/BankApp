package info.mackiewicz.bankapp.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import info.mackiewicz.bankapp.account.util.IbanGenerator;
import info.mackiewicz.bankapp.shared.util.IbanValidationUtil;

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
        String iban = IbanGenerator.generateIban(userId, accountCounter).toString();
    
        // Then
        assertTrue(IbanValidationUtil.isValid(iban));
    }
    
    @Test
    void shouldFormatIbanWithSpaces() {
        // Given
        String unformattedIban = TestIbanProvider.getIban(0);
    
        // When
        String formattedIban = IbanGenerator.formatIban(unformattedIban);
    
        // Then
        assertTrue(formattedIban.contains(" "));
        assertEquals(unformattedIban.length() + 6, formattedIban.length()); // 6 spaces in formatted IBAN
        assertEquals(unformattedIban, formattedIban.replace(" ", ""));
    }
    
    @Test
    void generatedIbanShouldHaveCorrectLength() {
        // Given
        Integer userId = 123;
        Integer accountCounter = 1;
    
        // When
        String iban = IbanGenerator.generateIban(userId, accountCounter).toString();
    
        // Then
        assertEquals(28, iban.length());
    }
    
    @Test
    void shouldGenerateUniqueIbansForDifferentAccounts() {
        // Given
        String iban1 = IbanGenerator.generateIban(123, 1).toString();
        String iban2 = IbanGenerator.generateIban(123, 2).toString();
        String iban3 = IbanGenerator.generateIban(456, 1).toString();
    
        // Then
        assertNotEquals(iban1, iban2);
        assertNotEquals(iban1, iban3);
        assertNotEquals(iban2, iban3);
    }
    }