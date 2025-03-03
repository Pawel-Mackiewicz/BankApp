package info.mackiewicz.bankapp.account.util;

import org.iban4j.Iban;
import org.iban4j.IbanFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class IbanConverterTest {

    private IbanConverter converter;
    private final String VALID_IBAN = "PL61109010140000071219812874";

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
        assertThrows(IbanFormatException.class, () -> 
            converter.convertToEntityAttribute(invalidIban)
        );
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

    @Test
    void convertToEntityAttribute_WithFormattedIban_ShouldHandleSpaces() {
        // given
        String formattedIban = "PL61 1090 1014 0000 0712 1981 2874";

        // when
        Iban result = converter.convertToEntityAttribute(formattedIban);

        // then
        assertNotNull(result);
        assertEquals(VALID_IBAN, result.toString());
    }
}