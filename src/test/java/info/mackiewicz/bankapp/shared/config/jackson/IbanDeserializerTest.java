package info.mackiewicz.bankapp.shared.config.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import info.mackiewicz.bankapp.transaction.exception.InvalidIbanException;
import info.mackiewicz.bankapp.utils.TestIbanProvider;

class IbanDeserializerTest {

    private IbanDeserializer deserializer;
    private JsonParser jsonParser;
    private DeserializationContext context;

    @BeforeEach
    void setUp() {
        deserializer = new IbanDeserializer();
        jsonParser = mock(JsonParser.class);
        context = mock(DeserializationContext.class);
    }

    @Test
    void shouldDeserializeValidIban() throws Exception {
        // given
        String validIban = TestIbanProvider.VALID_IBANS[0];
        when(jsonParser.getValueAsString()).thenReturn(validIban);

        // when
        Iban result = deserializer.deserialize(jsonParser, context);

        // then
        assertNotNull(result);
        assertEquals(validIban, result.toString());
    }

    @Test
    void shouldThrowExceptionForEmptyString() throws IOException {
        // given
        when(jsonParser.getValueAsString()).thenReturn("");

        // when & then
        InvalidIbanException exception = assertThrows(InvalidIbanException.class,
                () -> deserializer.deserialize(jsonParser, context));
        assertEquals("IBAN nie może być pusty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNull() throws IOException {
        // given
        when(jsonParser.getValueAsString()).thenReturn(null);

        // when & then
        InvalidIbanException exception = assertThrows(InvalidIbanException.class,
                () -> deserializer.deserialize(jsonParser, context));
        assertEquals("IBAN nie może być pusty", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "PL1234",
            "XX00000000000000000000"  // Nieprawidłowy kod kraju
    })
    void shouldThrowExceptionForInvalidFormat(String invalidIban) throws IOException {
        // given
        when(jsonParser.getValueAsString()).thenReturn(invalidIban);

        // when & then
        assertThrows(InvalidIbanException.class,
                () -> deserializer.deserialize(jsonParser, context));
    }

    @Test
    void shouldThrowExceptionForInvalidChecksum() throws IOException {
        // given
        String ibanWithInvalidChecksum = "PL12485112340003740000000001"; // Zmienione cyfry kontrolne
        when(jsonParser.getValueAsString()).thenReturn(ibanWithInvalidChecksum);

        // when & then
        InvalidIbanException exception = assertThrows(InvalidIbanException.class,
                () -> deserializer.deserialize(jsonParser, context));
        assertTrue(exception.getMessage().contains("suma kontrolna"));
    }

    @Test
    void shouldNormalizeIbanString() throws Exception {
        // given
        String ibanWithSpacesAndLowerCase = "pl34 4851 1234 0003 7400 0000 0001";
        String expectedIban = "PL34485112340003740000000001";
        when(jsonParser.getValueAsString()).thenReturn(ibanWithSpacesAndLowerCase);

        // when
        Iban result = deserializer.deserialize(jsonParser, context);

        // then
        assertNotNull(result);
        assertEquals(expectedIban, result.toString());
    }
}