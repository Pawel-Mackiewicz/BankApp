package info.mackiewicz.bankapp.controller;

import info.mackiewicz.bankapp.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final UserRegistrationService registrationService;

    public RegistrationController(UserRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

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
            return "registration";
        }
        
        try {
            @SuppressWarnings("unused")
            User createdUser = registrationService.registerUser(userRegistrationDto);
            redirectAttributes.addFlashAttribute("success", "Registration successful! You can now log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            logger.error("Registration error: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
    }
}