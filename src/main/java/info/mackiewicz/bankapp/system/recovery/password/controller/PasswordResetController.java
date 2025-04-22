package info.mackiewicz.bankapp.system.recovery.password.controller;

import info.mackiewicz.bankapp.shared.annotations.ValidEmail;
import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordResetDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
public interface PasswordResetController {

    @RequestMapping("/reset-request")
    public ResponseEntity<Void> requestReset(@ValidEmail String email);

    @RequestMapping("/reset-complete")
    ResponseEntity<Void> completeReset(@Valid PasswordResetDTO request);

}
