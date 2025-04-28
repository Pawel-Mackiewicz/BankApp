package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.core.user.model.User;
import lombok.NonNull;

public interface AccountAuthorizationService<T> {
    void validateAccountOwnership(@NonNull T accountIdentifier, @NonNull User owner);

    /**
     * Retrieves the name of the identifier type used for account authorization.
     *
     * @return a string representing the type of the account identifier
     */
    String getIdentifierTypeName();
}
