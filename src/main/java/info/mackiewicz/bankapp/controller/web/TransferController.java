package info.mackiewicz.bankapp.controller.web;

import info.mackiewicz.bankapp.dto.ExternalAccountTransferRequest;
import info.mackiewicz.bankapp.dto.InternalAccountTransferRequest;
import info.mackiewicz.bankapp.dto.OwnAccountTransferRequest;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionType;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.TransactionService;
import info.mackiewicz.bankapp.model.TransactionBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final TransactionBuilder transactionBuilder;

    @PostMapping("/own")
    public String handleOwnTransfer(
            @AuthenticationPrincipal User user,
            OwnAccountTransferRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            validateAccountOwnership(user, request.getSourceAccountId());
            validateAccountOwnership(user, request.getDestinationAccountId());

            Transaction transaction = transactionBuilder
                    .withSourceAccount(request.getSourceAccountId())
                    .withDestinationAccount(request.getDestinationAccountId())
                    .withAmount(new BigDecimal(request.getAmount()))
                    .withTransactionTitle(request.getTitle())
                    .withType(TransactionType.TRANSFER_OWN)
                    .build();

            transactionService.createTransaction(transaction);
            redirectAttributes.addFlashAttribute("successMessage", "Transfer between own accounts created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/internal")
    public String handleInternalTransfer(
            @AuthenticationPrincipal User user,
            @Valid InternalAccountTransferRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        try {
            if (!request.isValid()) {
                throw new IllegalArgumentException("Either IBAN or email must be provided");
            }
            validateAccountOwnership(user, request.getSourceAccountId());

            // Get destination account by IBAN
            Account destinationAccount = accountService.findByIban(request.getRecipientIban())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid recipient IBAN"));

            Transaction transaction = transactionBuilder
                    .withSourceAccount(request.getSourceAccountId())
                    .withDestinationAccount(destinationAccount.getId())
                    .withAmount(new BigDecimal(request.getAmount()))
                    .withTransactionTitle(request.getTitle())
                    .withType(TransactionType.TRANSFER_INTERNAL)
                    .build();

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
            ExternalAccountTransferRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            validateAccountOwnership(user, request.getSourceAccountId());

            Transaction transaction = transactionBuilder
                    .withSourceAccount(request.getSourceAccountId())
                    .withAmount(new BigDecimal(request.getAmount()))
                    .withTransactionTitle(request.getTitle())
                    .withType(TransactionType.TRANSFER_EXTERNAL)
                    .build();

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
}