package info.mackiewicz.bankapp.presentation.dashboard.controller;

import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.presentation.dashboard.service.SettingsService;
import info.mackiewicz.bankapp.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/settings")
public class SettingsRestController {

    private final SettingsService settingsService;

    @GetMapping("/user")
    public ResponseEntity<User> getUserSettings(@AuthenticationPrincipal User user) {
        User requestedUser = settingsService.getUserSettings(user.getId());
        
        if (!requestedUser.getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(requestedUser);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        
        boolean changed = settingsService.changePassword(user, request);
        if (changed) {
            String logoutUrl = UriComponentsBuilder.fromUriString("/logout").build().toUriString();
            return ResponseEntity.ok().header("Location", logoutUrl)
                    .body("Password changed successfully. Redirecting to logout...");
        }
        return ResponseEntity.badRequest().body("Failed to change password");
    }

    @PostMapping("/change-username")
    public ResponseEntity<Void> changeUsername(@AuthenticationPrincipal User user, @RequestBody ChangeUsernameRequest request) {
        settingsService.changeUsername(user, request);
        return ResponseEntity.ok().build();
    }
}