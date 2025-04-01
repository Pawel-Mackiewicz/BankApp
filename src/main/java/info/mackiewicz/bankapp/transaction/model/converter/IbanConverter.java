package info.mackiewicz.bankapp.transaction.model.converter;

import org.iban4j.Iban;
import org.iban4j.IbanFormatException;
import org.iban4j.InvalidCheckDigitException;
import org.iban4j.UnsupportedCountryException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.transaction.exception.InvalidIbanException;

/**
 * Converter for IBAN strings.
 * Automatically converts string values to Iban objects in Spring Framework.
 */
@Component
public class IbanConverter implements Converter<String, Iban> {

    @Override
    public Iban convert(@NonNull String source) {
        if (source == null || source.isEmpty()) {
            throw new InvalidIbanException("IBAN cannot be empty");
        }
        
        try {
            // Remove whitespace and convert to uppercase
            String sanitizedIban = source.replaceAll("\\s+", "").toUpperCase();
            return Iban.valueOf(sanitizedIban);
        } catch (IbanFormatException e) {
            throw new InvalidIbanException("Invalid IBAN format: " + e.getMessage(), e);
        } catch (InvalidCheckDigitException e) {
            throw new InvalidIbanException("Invalid check digit in IBAN: " + e.getMessage(), e);
        } catch (UnsupportedCountryException e) {
            throw new InvalidIbanException("Unsupported country code in IBAN: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new InvalidIbanException("Error processing IBAN: " + e.getMessage(), e);
        }
    }
}