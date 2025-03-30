package info.mackiewicz.bankapp.presentation.dashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.UserSettingsDTO;
import info.mackiewicz.bankapp.presentation.dashboard.service.SettingsService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.interfaces.PersonalInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/settings")
public class SettingsRestController implements SettingsRestControllerInterface {

    private final SettingsService settingsService;

    @Override
    @GetMapping("/user")
    public ResponseEntity<UserSettingsDTO> getUserSettings(@AuthenticationPrincipal PersonalInfo user) {
        return ResponseEntity.ok(settingsService.getUserSettings(user));
    }

    @Override
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        
        boolean changed = settingsService.changePassword(user, request);
        if (changed) {
            String logoutUrl = UriComponentsBuilder.fromUriString("/logout").build().toUriString();
            return ResponseEntity.ok().header("Location", logoutUrl)
                    .body("Password changed successfully. Redirecting to logout...");
        }
        return ResponseEntity.badRequest().body("Failed to change password");
    }

    @Override
    @PostMapping("/change-username")
    public ResponseEntity<Void> changeUsername(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangeUsernameRequest request) {
        settingsService.changeUsername(user, request);
        return ResponseEntity.ok().build();
    }
}