package info.mackiewicz.bankapp.account.model.dto;

import info.mackiewicz.bankapp.user.model.User;
import lombok.Getter;

@Getter
public class AccountOwnerDTO {
    private final Integer id;
    private final String fullName;

    public AccountOwnerDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
    }
}