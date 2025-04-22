package info.mackiewicz.bankapp.system.error.handling.dto.interfaces;

/**
 * Interface for DTOs that require password confirmation.
 * This interface defines the contract for any class that needs to implement
 * password confirmation functionality, typically used in user registration or password reset scenarios.
 */
public interface PasswordConfirmation {

    String getPassword();
    String getConfirmPassword();
}
