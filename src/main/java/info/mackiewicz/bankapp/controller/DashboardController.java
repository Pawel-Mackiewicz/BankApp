package info.mackiewicz.bankapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import info.mackiewicz.bankapp.dto.DashboardDTO;
import info.mackiewicz.bankapp.dto.TransferForm;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.DashboardService;
import jakarta.validation.Valid;

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
        model.addAttribute("transferForm", new TransferForm());  // To jest konieczne!
        model.addAttribute("userName", user.getUsername());
        return "dashboard";
    }

    @PostMapping("/transfer")
    public String makeTransfer(@Valid TransferForm transferForm, 
                             BindingResult bindingResult,
                             @AuthenticationPrincipal User user,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "dashboard";
        }

        try {
            dashboardService.processTransfer(transferForm, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Transfer successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/dashboard";
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