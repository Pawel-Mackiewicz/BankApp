package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class IdAccountAuthorizationService extends AbstractAccountAuthorizationService<Integer> {

    public IdAccountAuthorizationService(UserService userService) {
        super(userService);
    }

    @Override
    protected boolean isOwner(Integer accountIdentifier, Set<Account> accounts) {
        return accounts.stream()
                .anyMatch(account -> account.getId().equals(accountIdentifier));
    }

    @Override
    public String getIdentifierTypeName() {
        return "ID";
    }
}
