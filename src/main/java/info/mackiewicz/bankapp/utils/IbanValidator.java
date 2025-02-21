package info.mackiewicz.bankapp.utils;

import org.iban4j.IbanUtil;

public class IbanValidator {

    public static boolean isValid(String iban) {
        return IbanUtil.isValid(iban);
    }
}
