package info.mackiewicz.bankapp.utils;

import org.iban4j.IbanUtil;

import lombok.experimental.UtilityClass;

@UtilityClass
public class IbanValidator {

    public boolean isValid(String iban) {
        return IbanUtil.isValid(iban);
    }
}
