package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationMapper;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultRegistrationService implements RegistrationService {

    @Value("${bankapp.registration.WelcomeBonusAmount:1000}")
    private BigDecimal defaultWelcomeBonusAmount;


    private final UserService userService;
    private final RegistrationMapper registrationMapper;
    private final AccountService accountService;
    private final BonusGrantingService bonusGrantingService;
    private final EmailService emailService;

    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest request) {
        MDC.put("Email Address", request.getEmail().toString());
        try {
            log.info("Starting user registration process for email: {}", request.getEmail());

            User user = registrationMapper.toUser(request);
            log.debug("Mapped registration DTO to User entity");

            User createdUser = userService.createUser(user);
            MDC.put("User ID", createdUser.getId().toString());
            log.debug("User created");

            Account newAccount = accountService.createAccount(createdUser.getId());
            MDC.put("Account ID", newAccount.getId().toString());
            log.debug("Created new account");

            bonusGrantingService.grantWelcomeBonus(newAccount.getIban(), defaultWelcomeBonusAmount);
            log.debug("Welcome bonus granted");

            emailService.sendWelcomeEmail(createdUser.getEmail().toString(), createdUser.getFullName(),
                    createdUser.getUsername());
            log.debug("Welcome email sent");
            log.info("Completed user registration process for user: {}", createdUser.getUsername());

            return registrationMapper.toResponse(createdUser);

        } finally {
            MDC.clear();
        }
    }
}
