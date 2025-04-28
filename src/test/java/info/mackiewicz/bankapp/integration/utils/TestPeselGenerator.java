package info.mackiewicz.bankapp.integration.utils;

import info.mackiewicz.bankapp.core.user.model.vo.Pesel;

/**
 * Utility class for generating valid test PESEL numbers.
 * PESEL is the Polish national identification number.
 */
class TestPeselGenerator {
    // Represents year 1999, month 01 in PESEL format
    private static final String YEAR_MONTH = "9901";

    /**
     * Generates a deterministic but unique PESEL number based on the provided hash code and index.
     * Uses a fixed birth year and month (1999-01) with a day derived from the index.
     *
     * @param hashCode A hash code used as a base for generating unique PESELs
     * @param index    An index value to further differentiate PESELs
     *
     * @return A valid Pesel object
     */
    public static Pesel generatePesel(int hashCode, int index) {
        // Format the unique sequence to be exactly 5 digits
        String uniqueSequence = String.format("%05d", (hashCode + index) % 100000);
        // Calculate a day between 1-28 based on the index
        String day = String.format("%02d", (index % 28) + 1);
        // Concatenate all parts to form a complete PESEL
        return new Pesel(YEAR_MONTH + day + uniqueSequence);
    }
}
