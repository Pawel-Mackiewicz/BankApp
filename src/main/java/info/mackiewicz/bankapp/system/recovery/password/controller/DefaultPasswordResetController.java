package info.mackiewicz.bankapp.system.recovery.password.controller;

import info.mackiewicz.bankapp.shared.annotations.ValidEmail;
import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordResetRequest;
import info.mackiewicz.bankapp.system.recovery.password.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/password")
@Validated
public class DefaultPasswordResetController implements PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-request/{email}")
    public ResponseEntity<Void> requestReset(@PathVariable @ValidEmail String email) {
        passwordResetService.requestReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-complete")
    public ResponseEntity<Void> completeReset(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.completeReset(request);
        return ResponseEntity.ok().build();
    }
}