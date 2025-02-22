package info.mackiewicz.bankapp.controller.web;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.AccountService;
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