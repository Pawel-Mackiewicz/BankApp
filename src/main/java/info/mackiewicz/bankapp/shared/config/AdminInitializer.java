package info.mackiewicz.bankapp.shared.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.AdminUser;
import info.mackiewicz.bankapp.user.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * This class initializes the admin user in the database if it does not already exist.
 * It checks for admin credentials in the .env file and creates an admin user with those credentials.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordService passwordService;

    @Value("${spring.security.user.name}")
    private String adminUsername;

    @Value("${spring.security.user.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        // Check for admin credentials in .env file

        if (isAdminCredentialsAbsent()) {
            log.warn("Admin credentials not provided in .env file. Skipping admin creation.");
            return;
        }

        if (isAdminUserExists()) {
            log.debug("Admin user already exists. Skipping creation.");
            return;
        }

        AdminUser adminUser = createAdminUser();
        adminUserRepository.save(adminUser);
    }

    private boolean isAdminCredentialsAbsent() {
        return adminUsername == null || adminUsername.isEmpty() ||
                adminPassword == null || adminPassword.isEmpty();
    }

    private boolean isAdminUserExists() {
        return adminUserRepository.findByUsername(adminUsername).isPresent();
    }
    private AdminUser createAdminUser() {
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(adminUsername);
        adminUser.setPassword(passwordService.ensurePasswordEncoded(adminPassword));

        log.info("Admin user successfully created with username: {}", adminUsername);
        return adminUser;
    }
}
