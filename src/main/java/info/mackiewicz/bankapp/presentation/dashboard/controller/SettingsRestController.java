package info.mackiewicz.bankapp.presentation.dashboard.controller;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.thymeleaf.enabled:true}")
    private boolean isThymeleafEnabled; // Feature flag. Controlled via environment variable
 
    @Override
    @GetMapping("/user")
    public ResponseEntity<UserSettingsDTO> getUserSettings(@AuthenticationPrincipal PersonalInfo user) {
        return ResponseEntity.ok(settingsService.getUserSettings(user));
    }
    @Override
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {

        settingsService.changePassword(user, request);
        if (isThymeleafEnabled) {
            String logoutUrl = UriComponentsBuilder.fromUriString("/logout").build().toUriString();

            return ResponseEntity.ok().header("Location", logoutUrl)
                    .body("Password changed successfully. Redirecting to logout...");
        } else {
            return ResponseEntity.ok().build();
        }
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