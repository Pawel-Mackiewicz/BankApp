package info.mackiewicz.bankapp.dto;

import info.mackiewicz.bankapp.model.User;
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