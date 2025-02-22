package info.mackiewicz.bankapp.controller.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import info.mackiewicz.bankapp.dto.DashboardDTO;
import info.mackiewicz.bankapp.dto.ExternalAccountTransferRequest;
import info.mackiewicz.bankapp.dto.InternalAccountTransferRequest;
import info.mackiewicz.bankapp.dto.OwnAccountTransferRequest;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.DashboardService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public String getDashboard(@AuthenticationPrincipal User user, Model model) {
        DashboardDTO dashboard = dashboardService.getDashboardData(user.getId());
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("ownTransferForm", new OwnAccountTransferRequest());
        model.addAttribute("internalTransferForm", new InternalAccountTransferRequest());
        model.addAttribute("externalTransferForm", new ExternalAccountTransferRequest());
        model.addAttribute("userName", user.getUsername());
        return "dashboard";
    }

    @PostMapping("/new-account")
    public String createNewAccount(@AuthenticationPrincipal User user,
                               RedirectAttributes redirectAttributes) {
        try {
            dashboardService.createNewAccount(user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "New account created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/dashboard";
    }
}