package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class IbanAccountAuthorizationService extends AbstractAccountAuthorizationService<Iban> {

    public IbanAccountAuthorizationService(UserService userService) {
        super(userService);
    }

    @Override
    protected boolean isOwner(Iban accountIdentifier, Set<Account> accounts) {
        return accounts.stream()
                .anyMatch(account -> account.getIban().equals(accountIdentifier));
    }

    @Override
    public String getIdentifierTypeName() {
        return "IBAN";
    }
}
