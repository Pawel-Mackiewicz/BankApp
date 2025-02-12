package info.mackiewicz.bankapp.controller;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.TransactionService;
import info.mackiewicz.bankapp.service.UserService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserService userService;
    private final TransactionService transactionService;

    public DashboardController(UserService userService, TransactionService transactionService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    //TODO: Implement dashboardDTO dashboardService and dashboardView(?)
    @GetMapping
    public String getDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        List<Transaction> recentTransactions = transactionService.getRecentTransactions(user.getId(), 5);

        model.addAttribute("account", user);
        model.addAttribute("recentTransactions", recentTransactions);
        model.addAttribute("userName", userDetails.getUsername());
        
        return "dashboard";
    }
}