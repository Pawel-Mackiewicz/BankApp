package info.mackiewicz.bankapp.shared.dto.interfaces;

/**
 * Interface representing a password confirmation mechanism.
 * This interface is used to ensure that the password and its confirmation match.
 */
public interface PasswordConfirmation {

    String getPassword();
    String getConfirmPassword();
}
