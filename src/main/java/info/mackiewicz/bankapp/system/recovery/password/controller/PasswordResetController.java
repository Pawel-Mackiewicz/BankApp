package info.mackiewicz.bankapp.system.recovery.password.controller;

import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordResetDTO;
import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordResetRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
public interface PasswordResetController {

    @RequestMapping("/reset-request")
    //delete dto, just take email as parameter (but check if validation works that way)
    public ResponseEntity<Void> requestReset(@Valid @RequestBody PasswordResetRequestDTO request);

    @RequestMapping("/reset-complete")
    ResponseEntity<Void> completeReset(@Valid PasswordResetDTO request);

}
