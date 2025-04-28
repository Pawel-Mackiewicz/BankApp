package info.mackiewicz.bankapp.core.account.validation;

import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.shared.util.IbanValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller responsible for handling validation-related requests.
 * Provides endpoints for validating IBANs and checking email existence in the system.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ValidationController {

    private final AccountService accountService;

    /**
     * Validates the format of an IBAN (International Bank Account Number).
     *
     * @param iban The IBAN to validate
     * @return ResponseEntity containing validation result and a message
     */
    @GetMapping("/validate-iban")
    public ResponseEntity<Map<String, Object>> validateIban(@RequestParam String iban) {
        try {
            boolean isValid = IbanValidationUtil.isValid(iban);
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

    /**
     * Validates if an email is associated with any existing account in the system.
     * Checks both the email field and username field (which might contain an email).
     *
     * @param email The email address to validate
     * @return ResponseEntity containing validation result and a message
     */
    @GetMapping("/validate-email")
    public ResponseEntity<Map<String, Object>> validateEmail(@RequestParam String email) {
        EmailAddress emailAddress;
        //check if
        try {
            emailAddress = new EmailAddress(email);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "valid", false,
                "message", "Invalid email address: " + e.getMessage()
            ));
        }
        try {
            // Check if an account exists with the provided email
            boolean hasAccount = accountService.existsByEmail(emailAddress);

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