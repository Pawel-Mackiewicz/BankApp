package info.mackiewicz.bankapp.utils;

import lombok.experimental.UtilityClass;
import org.iban4j.CountryCode;
import org.iban4j.Iban;

@UtilityClass
public class IbanGenerator {

    public String generateIban(Integer userId, Integer userAccountNumber) {
        return new Iban.Builder()
                .countryCode(CountryCode.PL)
                .bankCode("485")
                .branchCode("1123")
                .nationalCheckDigit("4")
                .accountNumber(generateAccountNumber(userId, userAccountNumber))
                .build()
                .toString();
    }

    public String formatIban(String iban) {
        return Iban.valueOf(iban).toFormattedString();
    }

    public String generateAccountNumber(Integer userId, Integer accountCounter) {
        // Encoded User ID
        String encodedId = String.format("%06d", userId * 17);
        
        // User Account Number (2 digits)
        String accountNum = String.format("%02d", accountCounter);
        
        // Middle filled with zeros (8 digits)
        String middle = "00000000";
        
        return encodedId + middle + accountNum;
    }
}
