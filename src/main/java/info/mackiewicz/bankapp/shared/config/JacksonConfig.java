package info.mackiewicz.bankapp.shared.config;

import org.iban4j.Iban;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import info.mackiewicz.bankapp.shared.config.jackson.IbanDeserializer;

@Configuration
public class JacksonConfig {

    private final IbanDeserializer ibanDeserializer;

    public JacksonConfig(IbanDeserializer ibanDeserializer) {
        this.ibanDeserializer = ibanDeserializer;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Iban.class, ibanDeserializer);
        objectMapper.registerModule(module);
        return objectMapper;
    }
}