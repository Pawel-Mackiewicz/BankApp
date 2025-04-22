package info.mackiewicz.bankapp.presentation.auth.controller;

import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordChangeForm;
import info.mackiewicz.bankapp.system.recovery.password.controller.dto.PasswordResetRequest;
import info.mackiewicz.bankapp.system.recovery.password.service.PasswordResetTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PasswordResetWebController {

    private final RestClient restClient;
    private final PasswordResetTokenService passwordResetTokenService;

    @GetMapping("/password-reset")
    public String showPasswordResetForm(Model model) {
        log.debug("Displaying password reset form");
        if (!model.containsAttribute("passwordResetRequestDTO")) {
            model.addAttribute("passwordResetRequestDTO", new PasswordResetRequest());
        }
        return "password-reset";
    }

    @PostMapping("/password-reset")
    public String handlePasswordResetRequest(
            @Valid @ModelAttribute("passwordResetRequestDTO") PasswordResetRequest requestDTO,
            BindingResult bindingResult,
            Model model) {

        log.debug("Processing password reset request for email: {}", requestDTO.getEmail());

        if (bindingResult.hasErrors()) {
            log.debug("Validation errors in reset request: {}", bindingResult.getAllErrors());
            return "password-reset";
        }

        try {
            restClient.post()
                    .uri("/api/password/reset-request")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestDTO)
                    .retrieve()
                    .toBodilessEntity();
            model.addAttribute("success", true);
            log.debug("Password reset request processed successfully");
            return "password-reset";
        } catch (RestClientResponseException e) {
            log.error("Error processing reset request: Status {}, Body {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            String errorMessage = "An error occurred while processing your request.";

            if (e.getStatusCode().value() == 429) {
                errorMessage = "Too many password reset attempts detected. Please check your email inbox.";
            }
            model.addAttribute("error", errorMessage);
            return "password-reset";
        } catch (Exception e) {
            log.error("Unexpected error during reset request: {}", e.getMessage());
            model.addAttribute("error", "An error occurred while processing your request.");
            return "password-reset";
        }
    }

    @GetMapping("/password-reset/token/{token}")
    public String showNewPasswordForm(@PathVariable String token, Model model) {

        if (!passwordResetTokenService.isTokenPresent(token)) {
            log.debug("Invalid token: {}", token);
            return "redirect:/login";
        }
        log.debug("Displaying new password form for token");
        if (!model.containsAttribute("passwordResetDTO")) {
            PasswordChangeForm passwordChangeForm = new PasswordChangeForm();
            passwordChangeForm.setToken(token);
            model.addAttribute("passwordResetDTO", passwordChangeForm);
        }
        model.addAttribute("token", token);
        return "password-reset-complete";
    }

    @PostMapping("/password-reset/token/{token}")
    public String handlePasswordReset(
            @PathVariable String token,
            @Valid @ModelAttribute("passwordResetDTO") PasswordChangeForm passwordChangeForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.debug("Processing password reset completion for token");

        if (bindingResult.hasErrors()) {
            log.debug("Validation errors in password reset: {}", bindingResult.getAllErrors());
            model.addAttribute("token", token);
            return "password-reset-complete";
        }

        try {
            restClient.post()
                    .uri("/api/password/reset-complete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(passwordChangeForm)
                    .retrieve()
                    .toBodilessEntity();
            redirectAttributes.addFlashAttribute("success", "Your password has been successfully reset. You can now log in with your new password.");
            log.debug("Password reset completed successfully");
            return "redirect:/login";
        } catch (RestClientResponseException e) {
            log.error("Error resetting password: Status {}, Body {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            model.addAttribute("error", "An error occurred while resetting your password. Please try again.");
            model.addAttribute("token", token);
            return "password-reset-complete";
        } catch (Exception e) {
            log.error("Unexpected error during password reset: {}", e.getMessage());
            model.addAttribute("error", "An error occurred while resetting your password. Please try again.");
            model.addAttribute("token", token);
            return "password-reset-complete";
        }
    }
}