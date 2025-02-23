package info.mackiewicz.bankapp.controller.web;

import info.mackiewicz.bankapp.converter.TransactionAssembler;
import info.mackiewicz.bankapp.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.dto.OwnTransferRequest;
import info.mackiewicz.bankapp.dto.TransferRequest;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/dashboard/transfer")
@RequiredArgsConstructor
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
            redirectAttributes.addFlashAttribute("successMessage",
                    "Transfer between own accounts created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
            redirectAttributes.addFlashAttribute("successMessage", "Internal bank transfer created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
            redirectAttributes.addFlashAttribute("successMessage", "External transfer created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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