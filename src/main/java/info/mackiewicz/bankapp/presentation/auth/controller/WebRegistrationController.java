package info.mackiewicz.bankapp.presentation.auth.controller;

import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationRequest;
import info.mackiewicz.bankapp.presentation.auth.service.UserRegistrationService;
import info.mackiewicz.bankapp.user.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.user.exception.UserValidationException;
import info.mackiewicz.bankapp.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class WebRegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(WebRegistrationController.class);
    private final UserRegistrationService registrationService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("userRegistrationDto")) {
            model.addAttribute("userRegistrationDto", new UserRegistrationRequest());
        }
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("userRegistrationDto") UserRegistrationRequest userRegistrationRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("userRegistrationDto", userRegistrationRequest);
            model.addAttribute("status", HttpStatus.BAD_REQUEST);
            return "registration";
        }
        
        try {
            User createdUser = registrationService.registerUser(userRegistrationRequest);
            redirectAttributes.addFlashAttribute("success", "Registration successful! You can now log in. ");
            redirectAttributes.addFlashAttribute("username", String.format("Your username is: %s",createdUser.getUsername()));
            return "redirect:/login";
        } catch (IllegalArgumentException | UserValidationException | DuplicatedUserException e) {
            logger.error("Registration error: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userRegistrationDto", userRegistrationRequest);
            model.addAttribute("status", HttpStatus.BAD_REQUEST);
            return "registration";
        }
    }
}