package info.mackiewicz.bankapp.account.model.dto;

import java.util.Objects;

import info.mackiewicz.bankapp.account.model.interfaces.OwnershipInfo;
import info.mackiewicz.bankapp.user.model.User;
import lombok.Getter;

@Getter
public class AccountOwnerDTO implements OwnershipInfo {
    private final Integer id;
    private final String fullName;

    public AccountOwnerDTO(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        this.id = user.getId();
        this.fullName = user.getFullName();
    }
}