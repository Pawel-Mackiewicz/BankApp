package info.mackiewicz.bankapp.controller.web;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    @GetMapping
    public String showTransactionHistory(@AuthenticationPrincipal User user, Model model) {
        // Get user's accounts
        var accounts = accountService.getAccountsByOwnersId(user.getId());
        
        // Get initial transactions for all user accounts
        List<Transaction> allTransactions = new ArrayList<>();
        for (var account : accounts) {
            allTransactions.addAll(transactionService.getRecentTransactions(account.getId(), 20));
        }

        model.addAttribute("transactions", allTransactions);
        model.addAttribute("userName", user.getUsername());
        
        return "transactions_history";
    }
}