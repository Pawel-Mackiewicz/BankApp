package info.mackiewicz.bankapp.system.transaction.processing.core.execution;

import info.mackiewicz.bankapp.transaction.model.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registry of transaction execution commands.
 * Maps transaction types to appropriate execution commands.
 */
@Slf4j
@Service
public class TransactionExecutorRegistry {
    
    private final Map<TransactionType, TransactionExecutor> commandMap;
    
    /**
     * Creates the command registry from the list of available implementations.
     * Spring will automatically inject all components implementing TransactionExecutionCommand.
     * If multiple commands are registered for the same transaction type, the last one will be used.
     *
     * @param commands list of commands to register
     */
    public TransactionExecutorRegistry(List<TransactionExecutor> commands) {
        this.commandMap = commands.stream()
                .collect(Collectors.toMap(
                    TransactionExecutor::getTransactionType,
                    Function.identity(),
                    (existing, replacement) -> replacement  // Keep the last command for duplicate types
                ));
        
        log.info("Registered {} transaction commands", commandMap.size());
        commands.forEach(command -> 
            log.debug("Registered command for type: {}", command.getTransactionType()));
    }
    
    /**
     * Returns a command for the given transaction type.
     *
     * @param type transaction type
     * @return execution command for the specified type
     * @throws IllegalArgumentException if no command is registered for the given type
     */
    public TransactionExecutor getCommand(TransactionType type) {
        TransactionExecutor command = commandMap.get(type);
        if (command == null) {
            throw new IllegalArgumentException("No command registered for transaction type: " + type);
        }
        return command;
    }
}
