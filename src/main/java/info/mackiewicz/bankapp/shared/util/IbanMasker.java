package info.mackiewicz.bankapp.shared.util;

import org.iban4j.Iban;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IbanMasker {

    /**
     * Masks an IBAN by retaining only the first and last four characters.
     * <p>
     * If the given IBAN is null or has fewer than four characters, the original IBAN is returned.
     * Otherwise, the middle part of the IBAN is replaced with four asterisks.
     * </p>
     *
     * @param iban the IBAN string to be masked
     * @return the masked IBAN, or the original IBAN if it is null or too short to mask
     */
    public String maskIban(String iban) {
        if (iban == null || iban.length() < 4) {
            return iban; // Return the original IBAN if it's null or too short to mask
        }
        return iban.substring(0, 4) + "****" + iban.substring(iban.length() - 4);
    }

    /**
     * Masks the specified IBAN object by converting it to its string representation and obscuring its middle section.
     *
     * <p>This method delegates masking to the {@link #maskIban(String)} method, which preserves only the first four and last four
     * characters of the IBAN. If the string representation of the IBAN is null or shorter than four characters, the original value is returned.
     *
     * @param iban the IBAN object to mask
     * @return the masked IBAN as a string
     */
    public String maskIban(Iban iban) {
        return maskIban(iban.toString());
    }
}
