package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.user.model.User;
import lombok.NonNull;

public interface AccountAuthorizationService<T> {
    void validateAccountOwnership(T accountIdentifier, @NonNull User owner);
}
