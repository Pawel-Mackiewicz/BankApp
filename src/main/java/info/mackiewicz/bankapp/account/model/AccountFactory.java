package info.mackiewicz.bankapp.account.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.service.AccountValidationService;
import info.mackiewicz.bankapp.account.util.IbanGenerator;
import info.mackiewicz.bankapp.user.model.User;
import lombok.RequiredArgsConstructor;

/**
     * Klasa odpowiedzialna za tworzenie obiektów konta
     */
    @RequiredArgsConstructor
    @Component
    public class AccountFactory {

        private final AccountValidationService validationService;
        
        public Account createAccount(User owner) {
            validationService.validateNewAccountOwner(owner);
            Integer userAccountNumber = owner.getNextAccountNumber();
            String iban = IbanGenerator.generateIban(owner.getId(), userAccountNumber);
            
            return new Account(owner, userAccountNumber, iban);
        }
    }