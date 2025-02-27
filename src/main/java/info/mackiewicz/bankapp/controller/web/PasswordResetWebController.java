package info.mackiewicz.bankapp.controller.web;

import info.mackiewicz.bankapp.dto.PasswordResetDTO;
import info.mackiewicz.bankapp.dto.PasswordResetRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PasswordResetWebController {

    private final RestTemplate restTemplate;

    @GetMapping("/password-reset")
    public String showPasswordResetForm(Model model) {
        log.debug("Displaying password reset form");
        if (!model.containsAttribute("passwordResetRequestDTO")) {
            model.addAttribute("passwordResetRequestDTO", new PasswordResetRequestDTO());
        }
        return "password-reset";
    }

    @PostMapping("/password-reset")
    public String handlePasswordResetRequest(
            @Valid @ModelAttribute("passwordResetRequestDTO") PasswordResetRequestDTO requestDTO,
            BindingResult bindingResult,
            Model model) {

        log.debug("Processing password reset request for email: {}", requestDTO.getEmail());
        
        if (bindingResult.hasErrors()) {
            log.debug("Validation errors in reset request: {}", bindingResult.getAllErrors());
            return "password-reset";
        }

        try {
            restTemplate.postForEntity("/api/password/reset-request", requestDTO, Void.class);
            model.addAttribute("success", true);
            log.debug("Password reset request processed successfully");
            return "password-reset";
        } catch (Exception e) {
            log.error("Error processing reset request: {}", e.getMessage());
            model.addAttribute("error", "An error occurred while processing your request.");
            return "password-reset";
        }
    }

    @GetMapping("/password-reset/token/{token}")
    public String showNewPasswordForm(@PathVariable String token, Model model) {
        log.debug("Displaying new password form for token");
        if (!model.containsAttribute("passwordResetDTO")) {
            PasswordResetDTO passwordResetDTO = new PasswordResetDTO();
            passwordResetDTO.setToken(token);
            model.addAttribute("passwordResetDTO", passwordResetDTO);
        }
        model.addAttribute("token", token);
        return "password-reset-complete";
    }

    @PostMapping("/password-reset/token/{token}")
    public String handlePasswordReset(
            @PathVariable String token,
            @Valid @ModelAttribute("passwordResetDTO") PasswordResetDTO passwordResetDTO,
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
            restTemplate.postForEntity("/api/password/reset-complete", passwordResetDTO, Void.class);
            redirectAttributes.addFlashAttribute("success", "Your password has been successfully reset. You can now log in with your new password.");
            log.debug("Password reset completed successfully");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage());
            model.addAttribute("error", "An error occurred while resetting your password. Please try again.");
            model.addAttribute("token", token);
            return "password-reset-complete";
        }
    }
}