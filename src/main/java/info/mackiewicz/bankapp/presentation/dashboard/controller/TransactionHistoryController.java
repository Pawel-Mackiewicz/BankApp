package info.mackiewicz.bankapp.presentation.dashboard.controller;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final AccountService accountService;

    @GetMapping
    public String showTransactions(@AuthenticationPrincipal User user, Model model) {
        List<Account> userAccounts = accountService.getAccountsByOwnersId(user.getId());
        model.addAttribute("accounts", userAccounts);
        model.addAttribute("selectedAccountId", userAccounts.get(0).getId());
        model.addAttribute("userName", user.getUsername());
        return "transactions_history";
    }
}