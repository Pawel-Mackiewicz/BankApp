package info.mackiewicz.bankapp.system.recovery.password.controller;

import info.mackiewicz.bankapp.shared.annotations.ValidEmail;
import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordResetDTO;
import info.mackiewicz.bankapp.system.recovery.password.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/password")
@Validated
public class DefaultPasswordResetController implements PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-request")
    public ResponseEntity<Void> requestReset(@ValidEmail String email) {
        passwordResetService.requestReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-complete")
    public ResponseEntity<Void> completeReset(
            @Valid @RequestBody PasswordResetDTO request
    ) {
        passwordResetService.completeReset(request);
        return ResponseEntity.ok().build();
    }
}
