package info.mackiewicz.bankapp.testutils.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.mackiewicz.bankapp.shared.config.WebMvcConfig;
import info.mackiewicz.bankapp.shared.infrastructure.logging.ApiErrorLogger;
import info.mackiewicz.bankapp.shared.infrastructure.logging.LoggingInterceptor;
import info.mackiewicz.bankapp.shared.web.error.mapping.ApiExceptionToErrorMapper;
import info.mackiewicz.bankapp.shared.web.error.validation.ValidationErrorProcessor;
import info.mackiewicz.bankapp.shared.web.response.RestResponseFactory;
import info.mackiewicz.bankapp.shared.web.util.RequestUriHandler;
import info.mackiewicz.bankapp.system.notification.email.EmailSender;
import info.mackiewicz.bankapp.system.notification.email.template.EmailTemplateProvider;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@TestConfiguration
@EnableWebMvc
public class TestConfig {

    @Bean
    public LoggingInterceptor loggingInterceptor() {
        return new LoggingInterceptor();
    }

    @Bean
    public WebMvcConfig webMvcConfig(LoggingInterceptor loggingInterceptor) {
        return new WebMvcConfig(loggingInterceptor);
    }

    @Bean
    @Primary
    public RestResponseFactory restResponseBuilder() {
        return new RestResponseFactory();
    }

    @Bean
    public RequestUriHandler requestUriHandler() {
        return new RequestUriHandler();
    }

    @Bean
    public ApiErrorLogger apiErrorLogger() {
        return new ApiErrorLogger(true);
    }

    @Bean
    public ApiExceptionToErrorMapper apiExceptionToErrorMapper() {
        return new ApiExceptionToErrorMapper();
    }

    @Bean
    public ValidationErrorProcessor validationErrorProcessor() {
        return new ValidationErrorProcessor();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    @Primary
    public EmailSender emailSender() {
        return Mockito.mock(EmailSender.class);
    }

    @Bean
    @Primary
    public EmailTemplateProvider emailTemplateProvider() {
        return Mockito.mock(EmailTemplateProvider.class);
    }

    @Bean
    @Primary
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }
}