package info.mackiewicz.bankapp.presentation.dashboard.controller;

import info.mackiewicz.bankapp.core.user.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @GetMapping
    public String showSettingsPage(@AuthenticationPrincipal User user, Model model) {
        // Add user name to the model for header display
        model.addAttribute("userName", user.getFullName());
        return "settings";
    }
}