package info.mackiewicz.bankapp.presentation.dashboard.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import info.mackiewicz.bankapp.user.model.User;

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