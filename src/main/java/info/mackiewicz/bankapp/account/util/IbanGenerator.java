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
     * Constructs a 16-digit account number for IBAN generation.
     * <p>
     * This method creates the account number by concatenating three components:
     * <ol>
     *   <li>A fixed 4-digit prefix ("0000").</li>
     *   <li>An 8-digit encoded user ID obtained by multiplying the provided user ID by 13.</li>
     *   <li>A 4-digit, zero-padded account counter.</li>
     * </ol>
     * The result conforms to the strict banking standard for Polish IBANs.
     * <p>
     * NOTE: This implementation adheres to a banking standard and should not be changed.
     *
     * @param userId the unique identifier of the user
     * @param accountCounter the sequential counter for the user's account
     * @return a 16-digit account number string complying with banking regulations
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
