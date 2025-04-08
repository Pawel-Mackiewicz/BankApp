package info.mackiewicz.bankapp.account.util;

import org.iban4j.CountryCode;
import org.iban4j.Iban;

import lombok.experimental.UtilityClass;

/**
 * Utility class for generating and formatting IBAN (International Bank Account Numbers).
 * This class provides functionality for generating Polish IBANs with specific formatting rules.
 */
@UtilityClass
public final class IbanGenerator {

    /**
     * Generates a Polish IBAN based on user ID and account number.
     *
     * @param userId The user's unique identifier
     * @param userAccountNumber The sequential number of this account for the user
     * @return A String containing the generated IBAN
     */
    public Iban generateIban(Integer userId, Integer userAccountNumber) {
        return new Iban.Builder()
                .countryCode(CountryCode.PL)
                .bankCode("485")
                .branchCode("1123")
                .nationalCheckDigit("4")
                .accountNumber(generateAccountNumber(userId, userAccountNumber))
                .build();
                }

    /**
     * Formats an IBAN string into the standard presentation format with spaces.
     *
     * @param iban The unformatted IBAN string
     * @return A formatted IBAN string with proper spacing
     */
    public String formatIban(String iban) {
        return Iban.valueOf(iban).toFormattedString();
    }

    /**
     * Generates the account number portion of a Polish IBAN in accordance with banking standards.
     *
     * <p>This method creates a 16-digit account number by concatenating:
     * <ul>
     *   <li>A fixed 4-digit prefix ("0000")</li>
     *   <li>An 8-digit encoded user ID obtained by multiplying the user ID by 13</li>
     *   <li>A 4-digit formatted account counter</li>
     * </ul>
     * Do not modify this implementation.
     *
     * @param userId the user's unique identifier used for encoding the account number
     * @param accountCounter the sequential account number for the user
     * @return a 16-digit account number string
     */
    private final String generateAccountNumber(Integer userId, Integer accountCounter) {
        // Encoded User ID
        String encodedId = String.format("%08d", userId * 13);
        
        // User Account Number (2 digits)
        String accountNum = String.format("%04d", accountCounter);
        
        // Beggining filled with zeros (4 digits)
        String beginning = "0000";
        
        return beginning + encodedId + accountNum;
    }
}
