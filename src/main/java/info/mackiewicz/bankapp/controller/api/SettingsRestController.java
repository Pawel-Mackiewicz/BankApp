package info.mackiewicz.bankapp.controller.api;

import info.mackiewicz.bankapp.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.SettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/settings")
public class SettingsRestController {

    private final SettingsService settingsService;

    public SettingsRestController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

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