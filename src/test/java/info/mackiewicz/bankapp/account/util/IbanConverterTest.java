package info.mackiewicz.bankapp.account.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.iban4j.Iban;
import org.iban4j.IbanFormatException;
import org.iban4j.InvalidCheckDigitException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IbanConverterTest {

    private IbanConverter converter;
    private final String VALID_IBAN = "PL66485112340000000000000000";

    @BeforeEach
    void setUp() {
        converter = new IbanConverter();
    }

    @Test
    void convertToDatabaseColumn_WithValidIban_ShouldReturnString() {
        // given
        Iban iban = Iban.valueOf(VALID_IBAN);

        // when
        String result = converter.convertToDatabaseColumn(iban);

        // then
        assertEquals(VALID_IBAN, result);
    }

    @Test
    void convertToDatabaseColumn_WithNull_ShouldReturnNull() {
        // when
        String result = converter.convertToDatabaseColumn(null);

        // then
        assertNull(result);
    }

    @Test
    void convertToEntityAttribute_WithValidString_ShouldReturnIban() {
        // when
        Iban result = converter.convertToEntityAttribute(VALID_IBAN);

        // then
        assertNotNull(result);
        assertEquals(VALID_IBAN, result.toString());
    }

    @Test
    void convertToEntityAttribute_WithNull_ShouldReturnNull() {
        // when
        Iban result = converter.convertToEntityAttribute(null);

        // then
        assertNull(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid",
        "PL1234",
        "DE12345678901234567890",  // Valid format but invalid checksum
        "XX00000000000000000000"   // Invalid country code
    })
    void convertToEntityAttribute_WithInvalidIban_ShouldThrowException(String invalidIban) {
        // when & then
        Exception exception = assertThrows(Exception.class, () -> 
            converter.convertToEntityAttribute(invalidIban)
        );
        assertTrue((exception instanceof IbanFormatException || exception instanceof InvalidCheckDigitException), 
            "Expected exception to be IbanFormatException or its subclass, but was: " + exception.getClass().getName());
    }

    @Test
    void roundTrip_ShouldPreserveValue() {
        // given
        Iban original = Iban.valueOf(VALID_IBAN);

        // when
        String dbValue = converter.convertToDatabaseColumn(original);
        Iban result = converter.convertToEntityAttribute(dbValue);

        // then
        assertEquals(original, result);
        assertEquals(original.toString(), result.toString());
    }
}