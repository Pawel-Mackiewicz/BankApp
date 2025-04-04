package info.mackiewicz.bankapp.shared.util;

import org.iban4j.Iban;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IbanMasker {

    /**
     * Masks the IBAN by replacing the middle part with asterisks.
     * The first 4 and last 4 characters remain visible.
     *
     * @param iban The IBAN to be masked
     * @return The masked IBAN
     */
    public String maskIban(String iban) {
        if (iban == null || iban.length() < 4) {
            return iban; // Return the original IBAN if it's null or too short to mask
        }
        return iban.substring(0, 4) + "****" + iban.substring(iban.length() - 4);
    }

    public String maskIban(Iban iban) {
        return maskIban(iban.toString());
    }
}
