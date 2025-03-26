package info.mackiewicz.bankapp.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank App API Documentation")
                        .version("0.4.5")
                        .description("API documentation for the Bank App project.")
                        .contact(new Contact()
                            .name("Pawel Mackiewicz")
                            .url("https://github.com/Pawel-Mackiewicz/BankApp")
                            .email("pawel@mackiewicz.info")
                            )
                        )
                ;
    }
}
