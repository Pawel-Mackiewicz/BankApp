package info.mackiewicz.bankapp.core.account.validation;

import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.account.validation.dto.ValidationResponse;
import info.mackiewicz.bankapp.core.user.exception.InvalidEmailFormatException;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.shared.util.IbanValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling validation-related requests.
 * Provides endpoints for validating IBANs and checking email existence in the system.
 */
@Slf4j
@RestController
@RequestMapping("/api/account/validate")
@RequiredArgsConstructor
public class ValidationController implements ValidationControllerInterface {

    private final AccountService accountService;

    @GetMapping("/iban")
    @Override
    public ResponseEntity<ValidationResponse> validateIban(@RequestParam String iban) {
        if (iban == null || iban.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ValidationResponse.invalid("Iban must not be empty")
            );
        }
        
        try {
            boolean isValid = IbanValidationUtil.isValid(iban);
            String message = isValid ? "Valid IBAN format" : "Invalid IBAN format";
            return isValid ?
                    ResponseEntity.ok().body(ValidationResponse.valid(message)) :
                    ResponseEntity.badRequest().body(ValidationResponse.invalid(message));
        } catch (Exception e) {
            log.error("Error during IBAN validation", e);
            return ResponseEntity.status(500).body(
                    ValidationResponse.invalid("Error during validation")
            );
        }
    }

    @GetMapping("/email")
    @Override
    public ResponseEntity<ValidationResponse> validateEmail(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ValidationResponse.invalid("Email must not be empty")
            );
        }
        
        EmailAddress emailAddress;
        try {
            emailAddress = new EmailAddress(email);
        } catch (InvalidEmailFormatException e) {
            return ResponseEntity.badRequest().body(
                    ValidationResponse.invalid("Invalid email address format")
            );
        }

        try {
            boolean found = accountService.existsByEmail(emailAddress);
            String message = found ? "Account found" : "No account found for this email";
            return found ?
                    ResponseEntity.ok().body(ValidationResponse.found(message)) :
                    ResponseEntity.status(404).body(ValidationResponse.notFound(message));
        } catch (Exception e) {
            log.error("Error during email validation", e);
            return ResponseEntity.status(500).body(
                    ValidationResponse.invalid("Internal error during validation")
            );
        }
    }
}