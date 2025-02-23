package info.mackiewicz.bankapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.iban4j.IbanUtil;

public class IbanValidator implements ConstraintValidator<Iban, String> {

    @Override
    public void initialize(Iban constraintAnnotation) {
    }

    @Override
    public boolean isValid(String iban, ConstraintValidatorContext context) {
        if (iban == null || iban.trim().isEmpty()) {
            return true;
        }
        return IbanUtil.isValid(iban);
    }
}