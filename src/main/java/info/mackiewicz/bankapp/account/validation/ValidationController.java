package info.mackiewicz.bankapp.account.validation;

import info.mackiewicz.bankapp.shared.util.IbanValidator;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ValidationController {

    private final AccountRepository accountRepository;

    @GetMapping("/validate-iban")
    public ResponseEntity<Map<String, Object>> validateIban(@RequestParam String iban) {
        try {
            boolean isValid = IbanValidator.isValid(iban);
            return ResponseEntity.ok(Map.of(
                "valid", isValid,
                "message", isValid ? "Valid IBAN format" : "Invalid IBAN format"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "message", "Error during validation: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/validate-email")
    public ResponseEntity<Map<String, Object>> validateEmail(@RequestParam String email) {
        try {
            // Szukamy pierwszego konta dla użytkownika o podanym emailu
            boolean hasAccount = accountRepository.findFirstByOwner_email(email).isPresent();
            if (!hasAccount) {
                // Try to find by username as email
                hasAccount = accountRepository.findAccountsByOwner_username(email)
                    .map(accounts -> !accounts.isEmpty())
                    .orElse(false);
            }
            return ResponseEntity.ok(Map.of(
                "valid", hasAccount,
                "message", hasAccount ? "Account found" : "No account found for this email"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "message", "Error during validation: " + e.getMessage()
            ));
        }
    }
}