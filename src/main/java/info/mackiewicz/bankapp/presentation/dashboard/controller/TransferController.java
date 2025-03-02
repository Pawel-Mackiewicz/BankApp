package info.mackiewicz.bankapp.presentation.dashboard.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.OwnTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionAssembler;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/dashboard/transfer")
public class TransferController {

    private final TransactionService transactionService;
    private final TransactionAssembler transactionAssembler;
    private final AccountService accountService;

    @PostMapping("/own")
    public String handleOwnTransfer(
            @AuthenticationPrincipal User user,
            OwnTransferRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            validateAccountOwnership(user, request.getSourceAccountId());
            validateAccountOwnership(user, request.getDestinationAccountId());

            Transaction transaction = transactionAssembler.assembleOwnTransfer(request);

            transactionService.createTransaction(transaction);
            redirectAttributes.addFlashAttribute("transferSuccessMessage",
                    "Transfer between own accounts created successfully");
            log.info("Flash success message set for own transfer: Transfer between own accounts created successfully");
         } catch (Exception e) {
             redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
             log.error("Flash error message set for own transfer: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/internal")
    public String handleInternalTransfer(
            @AuthenticationPrincipal User user,
            @Valid InternalTransferRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        try {
            if (!request.isValid()) {
                throw new IllegalArgumentException("Either IBAN or email must be provided");
            }
            validateAccountOwnership(user, request.getSourceIban());

            log.info(": -> Request: " + request);
            
            Transaction transaction = transactionAssembler.assembleInternalTransfer(request);

            transactionService.createTransaction(transaction);
            redirectAttributes.addFlashAttribute("transferSuccessMessage", "Internal bank transfer created successfully");
            log.info("Flash success message set for internal transfer: Internal bank transfer created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error("Flash error message set for internal transfer: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/external")
    public String handleExternalTransfer(
            @AuthenticationPrincipal User user,
            TransferRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            validateAccountOwnership(user, request.getSourceIban());

            Transaction transaction = transactionAssembler.assembleExternalTransfer(request);

            transactionService.createTransaction(transaction);
            redirectAttributes.addFlashAttribute("transferSuccessMessage", "External transfer created successfully");
            log.info("Flash success message set for external transfer: External transfer created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            log.error("Flash error message set for external transfer: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    private void validateAccountOwnership(User user, Integer accountId) {
        if (!accountService.getAccountsByOwnersId(user.getId()).stream()
                .anyMatch(account -> account.getId().equals(accountId))) {
            throw new IllegalArgumentException("You don't have permission to this account");
        }
    }

    private void validateAccountOwnership(User user, String accountIban) {
        if (!accountService.getAccountsByOwnersId(user.getId()).stream()
                .map(Account::getIban)
                .anyMatch(iban -> iban.equals(accountIban))) {
            throw new IllegalArgumentException("You don't have permission to this account");
        }
    }
}