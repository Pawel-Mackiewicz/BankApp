package info.mackiewicz.bankapp.core.account.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.iban4j.Iban;

/**
 * JPA converter for IBAN objects.
 * Handles conversion between Iban objects and their String representation
 * for database persistence purposes.
 */
@Converter
public class IbanConverter implements AttributeConverter<Iban, String> {

    /**
     * Converts an Iban object to a String representation for database storage.
     *
     * @param attribute the Iban object to convert
     * @return String representation of the Iban or null if the input is null
     */
    @Override
    public String convertToDatabaseColumn(Iban attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    /**
     * Converts a String representation from database to an Iban object.
     *
     * @param dbData the String value from database
     * @return the Iban object or null if the input is null
     */
    @Override
    public Iban convertToEntityAttribute(String dbData) {
        return dbData != null ? Iban.valueOf(dbData) : null;
    }
}