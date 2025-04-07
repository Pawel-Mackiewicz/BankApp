package info.mackiewicz.bankapp.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("!test")
public class CheckDefaultCredentials implements CommandLineRunner {

    private static final String DEFAULT_USERNAME = "testuser";
    private static final String DEFAULT_PASSWORD = "testpass";

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {

        if (username.equals(DEFAULT_USERNAME) && password.equals(DEFAULT_PASSWORD)) {
            log.error("Default credentials are still in use. Please change them.");
            throw new RuntimeException("Default credentials are still in use. Please change them.");
        } else {
            log.info("Default credentials have been changed. Application is secure.");
        }
    }
        

}
