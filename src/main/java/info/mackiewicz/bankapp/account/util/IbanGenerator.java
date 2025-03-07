package info.mackiewicz.bankapp.account.util;

import org.iban4j.CountryCode;
import org.iban4j.Iban;

import lombok.experimental.UtilityClass;

/**
 * Utility class for generating and formatting IBAN (International Bank Account Numbers).
 * This class provides functionality for generating Polish IBANs with specific formatting rules.
 */
@UtilityClass
public class IbanGenerator {

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
     * Generates the account number part of the IBAN.
     * 
     * @param userId The user's unique identifier
     * @param accountCounter The sequential number of the account for the user
     * @return A 16-digit account number string
     */
    private String generateAccountNumber(Integer userId, Integer accountCounter) {
        // Encoded User ID
        String encodedId = String.format("%06d", userId * 17);
        
        // User Account Number (2 digits)
        String accountNum = String.format("%02d", accountCounter);
        
        // Middle filled with zeros (8 digits)
        String middle = "00000000";
        
        return encodedId + middle + accountNum;
    }
}
