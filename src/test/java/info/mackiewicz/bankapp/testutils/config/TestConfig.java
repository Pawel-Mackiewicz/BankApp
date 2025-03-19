package info.mackiewicz.bankapp.testutils.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import info.mackiewicz.bankapp.notification.email.EmailSender;
import info.mackiewicz.bankapp.notification.email.template.EmailTemplateProvider;
import info.mackiewicz.bankapp.shared.config.WebMvcConfig;
import info.mackiewicz.bankapp.shared.exception.handler.ApiErrorLogger;
import info.mackiewicz.bankapp.shared.exception.handler.ApiExceptionToErrorMapper;
import info.mackiewicz.bankapp.shared.exception.handler.RequestUriHandler;
import info.mackiewicz.bankapp.shared.exception.handler.ValidationErrorProcessor;
import info.mackiewicz.bankapp.shared.interceptor.LoggingInterceptor;
import info.mackiewicz.bankapp.shared.util.ApiResponseBuilder;

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
    public ApiResponseBuilder apiResponseBuilder() {
        return new ApiResponseBuilder();
    }

    @Bean
    public RequestUriHandler requestUriHandler() {
        return new RequestUriHandler();
    }

    @Bean
    public ApiErrorLogger apiErrorLogger() {
        return new ApiErrorLogger();
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