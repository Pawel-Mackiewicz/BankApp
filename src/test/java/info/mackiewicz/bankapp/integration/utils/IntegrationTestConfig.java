package info.mackiewicz.bankapp.integration.utils;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    public IntegrationTestUserService testUserService() {
        return new IntegrationTestUserService();
    }

    @Bean
    public IntegrationTestAccountService testAccountService() {
        return new IntegrationTestAccountService();
    }
}
