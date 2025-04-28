package info.mackiewicz.bankapp.shared.util;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.iban4j.Iban;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Provides access to a default bank account associated with the application.
 * This class acts as a utility to retrieve a specific bank account based on its predefined IBAN.
 * The account retrieval is handled via the {@link AccountService}.
 *
 * The default IBAN is hardcoded as a constant and refers to a Polish IBAN (PL66485112340000000000000000).
 */
@Component
@RequiredArgsConstructor
public class BankAccountProvider {

    @Value("${bankapp.bank.account.iban:PL66485112340000000000000000}")
    private String defaultBankIbanString;
    private Iban defaultBankIban;

    @PostConstruct
    private void init() {defaultBankIban = Iban.valueOf(defaultBankIbanString);}

    private final AccountService accountService;

    public Account getBankAccount() {
        return accountService.getAccountByIban(defaultBankIban);
    }

}
