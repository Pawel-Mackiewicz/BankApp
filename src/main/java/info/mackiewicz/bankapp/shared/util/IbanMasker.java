package info.mackiewicz.bankapp.shared.util;

import org.iban4j.Iban;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IbanMasker {

    /**
     * Masks an IBAN by preserving its first four and last four characters while replacing the middle section with four asterisks.
     * <p>
     * If the IBAN is null or shorter than four characters, the original IBAN is returned unchanged.
     * </p>
     *
     * @param iban the IBAN to mask
     * @return the masked IBAN, or the original IBAN if no masking is applied
     */
    public String maskIban(String iban) {
        if (iban == null || iban.length() < 4) {
            return iban; // Return the original IBAN if it's null or too short to mask
        }
        return iban.substring(0, 4) + "****" + iban.substring(iban.length() - 4);
    }

    /**
     * Masks the provided IBAN by converting it to a string and delegating to the string masking method.
     *
     * <p>The masking retains the first four and last four characters of the IBAN while replacing the middle
     * section with asterisks. If the string representation of the IBAN is null or too short to mask, it is returned unchanged.</p>
     *
     * @param iban the IBAN object to be masked
     * @return the masked IBAN as a string
     */
    public String maskIban(Iban iban) {
        return maskIban(iban.toString());
    }
}
