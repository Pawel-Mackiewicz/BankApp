package info.mackiewicz.bankapp.controller.web;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
 
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import info.mackiewicz.bankapp.dto.DashboardDTO;
import info.mackiewicz.bankapp.dto.ExternalTransferRequest;
import info.mackiewicz.bankapp.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.dto.OwnTransferRequest;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.DashboardService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
 
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
 
    @GetMapping
    public String getDashboard(@AuthenticationPrincipal User user, Model model, HttpServletRequest request) {
        DashboardDTO dashboard = dashboardService.getDashboardData(user.getId());
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("ownTransferForm", new OwnTransferRequest());
        model.addAttribute("internalTransferForm", new InternalTransferRequest());
        model.addAttribute("externalTransferForm", new ExternalTransferRequest());
        model.addAttribute("userName", user.getUsername());
        Map<String, ?> flashMap = org.springframework.web.servlet.support.RequestContextUtils.getInputFlashMap(request);
        if (flashMap != null) {
            model.addAllAttributes(flashMap);
            log.info("Flash attributes added to model: {}", flashMap);
        } else {
            log.info("No flash attributes found in request");
        }
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