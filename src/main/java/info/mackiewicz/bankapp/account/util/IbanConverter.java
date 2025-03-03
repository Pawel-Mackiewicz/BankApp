package info.mackiewicz.bankapp.account.util;

import org.iban4j.Iban;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IbanConverter implements AttributeConverter<Iban, String> {

    @Override
    public String convertToDatabaseColumn(Iban attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public Iban convertToEntityAttribute(String dbData) {
        return dbData != null ? Iban.valueOf(dbData) : null;
    }
}