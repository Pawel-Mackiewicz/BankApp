package info.mackiewicz.bankapp.presentation.auth.controller;

import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.presentation.auth.service.UserRegistrationService;
import info.mackiewicz.bankapp.user.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.user.exception.UserValidationException;
import info.mackiewicz.bankapp.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Controller
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final UserRegistrationService registrationService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("userRegistrationDto")) {
            model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        }
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto userRegistrationDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("userRegistrationDto", userRegistrationDto);
            model.addAttribute("status", HttpStatus.BAD_REQUEST);
            return "registration";
        }
        
        try {
            User createdUser = registrationService.registerUser(userRegistrationDto);
            redirectAttributes.addFlashAttribute("success", "Registration successful! You can now log in. ");
            redirectAttributes.addFlashAttribute("username", String.format("Your username is: %s",createdUser.getUsername()));
            return "redirect:/login";
        } catch (IllegalArgumentException | UserValidationException | DuplicatedUserException e) {
            logger.error("Registration error: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userRegistrationDto", userRegistrationDto);
            model.addAttribute("status", HttpStatus.BAD_REQUEST);
            return "registration";
        }
    }
}