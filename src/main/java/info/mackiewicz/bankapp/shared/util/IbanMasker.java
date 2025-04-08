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

    /**
     * Masks an IBAN by converting the provided IBAN object to its string representation and then obscuring its middle portion.
     *
     * <p>This method retains the first four and the last four characters of the IBAN, replacing any intermediate characters with asterisks.
     * The actual masking logic is delegated to the string-based maskIban method.</p>
     *
     * @param iban the IBAN object to mask
     * @return a masked IBAN string with only the first and last four characters visible
     */
    public String maskIban(Iban iban) {
        return maskIban(iban.toString());
    }
}
