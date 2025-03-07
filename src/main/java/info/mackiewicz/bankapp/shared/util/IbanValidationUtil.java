package info.mackiewicz.bankapp.shared.util;

import lombok.experimental.UtilityClass;
import org.iban4j.IbanUtil;

/**
 * Klasa narzędziowa zawierająca wspólną logikę walidacji numerów IBAN.
 * Używana przez różne walidatory w aplikacji.
 */
@UtilityClass
public class IbanValidationUtil {
    
    /**
     * Sprawdza, czy podany numer IBAN jest poprawny.
     * 
     * @param iban Numer IBAN do sprawdzenia
     * @return true jeśli IBAN jest poprawny, false w przeciwnym przypadku
     */
    public boolean isValid(String iban) {
        if (iban == null || iban.trim().isEmpty()) {
            return false;
        }
        try {
            return IbanUtil.isValid(iban);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Sprawdza, czy podany numer IBAN jest poprawny w kontekście walidacji Bean Validation.
     * Różni się od zwykłej walidacji tym, że puste wartości są traktowane jako poprawne
     * (walidacja pustych wartości powinna być obsługiwana przez adnotacje @NotNull/@NotEmpty).
     * 
     * @param iban Numer IBAN do sprawdzenia
     * @return true jeśli IBAN jest poprawny lub pusty, false jeśli jest niepoprawny
     */
    public boolean isValidForBeanValidation(String iban) {
        if (iban == null || iban.trim().isEmpty()) {
            return true; // Puste wartości są akceptowane w kontekście Bean Validation
        }
        try {
            return IbanUtil.isValid(iban);
        } catch (Exception e) {
            return false;
        }
    }
}