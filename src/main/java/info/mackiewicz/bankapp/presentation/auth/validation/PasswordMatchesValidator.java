package info.mackiewicz.bankapp.presentation.auth.validation;

/**
 * Validates that the password and confirmation password match.
 * Null values are considered valid to allow @NotBlank validation to handle them.
 * When passwords don't match, adds a constraint violation to the confirmPassword field.
 */
import info.mackiewicz.bankapp.shared.dto.interfaces.PasswordConfirmation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordConfirmation> {

    @Override
    public boolean isValid(PasswordConfirmation dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
            return true; // Let @NotBlank handle null validation
        }
        
        boolean passwordsMatch = dto.getPassword().equals(dto.getConfirmPassword());
        if (!passwordsMatch) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                   .addPropertyNode("confirmPassword")
                   .addConstraintViolation();
        }
        return passwordsMatch;
    }
}
