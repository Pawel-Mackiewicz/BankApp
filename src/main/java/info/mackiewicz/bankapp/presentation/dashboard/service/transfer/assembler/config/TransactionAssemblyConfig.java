package info.mackiewicz.bankapp.presentation.dashboard.service.transfer.assembler.config;

import info.mackiewicz.bankapp.presentation.dashboard.service.transfer.assembler.TransactionAssemblyStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Configuration class for transaction assembly strategies.
 */
@Configuration
public class TransactionAssemblyConfig {

    /**
     * Creates a map of request types to their corresponding assembly strategies.
     * The map is used by TransactionAssembler to select appropriate strategy
     * based on the request type.
     *
     * @param strategies lista wszystkich dostępnych strategii
     * @return mapa typu requestu do odpowiedniej strategii
     */
    @Bean
    public Map<Class<?>, TransactionAssemblyStrategy<?>> assemblyStrategies(List<TransactionAssemblyStrategy<?>> strategies) {
        return strategies.stream()
                .collect(Collectors.toMap(
                    TransactionAssemblyStrategy::getSupportedRequestType,
                    Function.identity()
                ));
    }
}