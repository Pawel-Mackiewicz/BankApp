package info.mackiewicz.bankapp.shared.config.jackson;

import java.io.IOException;

import org.iban4j.Iban;
import org.iban4j.IbanFormatException;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import info.mackiewicz.bankapp.transaction.exception.InvalidIbanException;

@Component
public class IbanDeserializer extends StdDeserializer<Iban> {

    public IbanDeserializer() {
        super(Iban.class);
    }

    @Override
    public Iban deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String source = p.getValueAsString();
        
        if (source == null || source.isEmpty()) {
            throw new InvalidIbanException("IBAN nie może być pusty");
        }
        
        try {
            String sanitizedIban = source.replaceAll("\\s+", "").toUpperCase();
            return Iban.valueOf(sanitizedIban);
        } catch (IbanFormatException e) {
            throw new InvalidIbanException("Nieprawidłowy format IBAN: " + e.getMessage(), e);
        } catch (InvalidCheckDigitException e) {
            throw new InvalidIbanException("Nieprawidłowa suma kontrolna IBAN: " + e.getMessage(), e);
        } catch (UnsupportedCountryException e) {
            throw new InvalidIbanException("Nieobsługiwany kod kraju w IBAN: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new InvalidIbanException("Błąd przetwarzania IBAN: " + e.getMessage(), e);
        }
    }
}