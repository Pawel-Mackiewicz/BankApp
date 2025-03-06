package info.mackiewicz.bankapp.utils;

import org.iban4j.Iban;

import info.mackiewicz.bankapp.account.util.IbanGenerator;

/**
 * Helper class providing test IBAN numbers for unit tests.
 * Combines both hardcoded valid IBANs and dynamic generation using IbanGenerator.
 */
public class TestIbanProvider {

    /**
     * List of valid, pre-defined IBAN numbers that can be used in tests
     */
    public static final String[] VALID_IBANS = {
        "PL34485112340003740000000001",
        "PL21485112340003570000000001",
        "PL92485112340003230000000001",
        "PL70485112340002380000000003",
        "PL97485112340002380000000002"
    };

    private static int currentIndex = 0;

    /**
     * Gets next IBAN from the predefined list using round-robin approach
     */
    public static String getNextIban() {
        String iban = VALID_IBANS[currentIndex];
        currentIndex = (currentIndex + 1) % VALID_IBANS.length;
        return iban;
    }

    /**
     * Gets specific IBAN from the predefined list
     */
    public static String getIban(int index) {
        return VALID_IBANS[index % VALID_IBANS.length];
    }

    /**
     * Generates a new IBAN using IbanGenerator
     */
    public static String generateIban(int userId, int accountNumber) {
        return IbanGenerator.generateIban(userId, accountNumber);
    }

    /**
     * Gets next IBAN as Iban object from the predefined list
     */
    public static Iban getNextIbanObject() {
        return Iban.valueOf(getNextIban());
    }

    /**
     * Gets specific IBAN as Iban object from the predefined list
     */
    public static Iban getIbanObject(int index) {
        return Iban.valueOf(getIban(index));
    }
}