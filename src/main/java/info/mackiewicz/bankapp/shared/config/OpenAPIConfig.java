package info.mackiewicz.bankapp.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "basicAuth";

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
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }

    //Add default 401 response to all entries in swagger, when 401 is not implemented.
    @Bean
    public OpenApiCustomizer global401ResponseCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    // Add when 401 not implemented yet
                    if (!operation.getResponses().containsKey("401") && operation.getSecurity() != null) {
                        ApiResponse unauthorizedResponse = new ApiResponse()
                                .description("Unauthorized – user is not authenticated")
                                .content(
                                        new Content().addMediaType("application/json",
                                                new MediaType()
                                                        .addExamples("Unauthorized", new Example()
                                                                .summary("Returned when the user tries to access this endpoint without being authorized or logged in.")
                                                                .value(Map.of(
                                                                        "error", "Unauthorized",
                                                                        "message", "Full authentication is required to access this resource"
                                                                ))
                                                        )
                                        )
                                );
                        operation.getResponses().addApiResponse("401", unauthorizedResponse);
                    }
                })
        );
    }


}
