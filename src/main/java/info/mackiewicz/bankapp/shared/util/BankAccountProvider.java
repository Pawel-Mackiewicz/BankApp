package info.mackiewicz.bankapp.shared.util;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.iban4j.Iban;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BankAccountProvider {

    private static final Iban DEFAULT_BANK_IBAN = Iban.valueOf("PL66485112340000000000000000");

    private final AccountService accountService;

    public Account getBankAccount() {
        return accountService.getAccountByIban(DEFAULT_BANK_IBAN);
    }

}
