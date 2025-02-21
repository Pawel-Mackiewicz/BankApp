package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.repository.AccountRepository;
import info.mackiewicz.bankapp.utils.IbanGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IbanMigrationService {

private final AccountRepository accountRepository;

@PostConstruct
@Transactional
void migrateExistingAccounts() {
    log.info("Starting IBAN migration for existing accounts");
    
    List<Account> accountsWithoutIban = accountRepository.findByIbanIsNull();
    
    if (accountsWithoutIban.isEmpty()) {
        log.info("No accounts found requiring IBAN migration");
        return;
    }

    log.info("Found {} accounts requiring IBAN migration", accountsWithoutIban.size());
    
    for (Account account : accountsWithoutIban) {
        String iban = IbanGenerator.generateIban(account.getOwner().getId(), account.getUserAccountNumber());
        account.setIban(iban);
        log.info("Generated IBAN {} for account ID {}", iban, account.getId());
    }

    accountRepository.saveAll(accountsWithoutIban);
    log.info("IBAN migration completed successfully");
}
}