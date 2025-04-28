package info.mackiewicz.bankapp.core.account.service;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Class controlling access to internal account operations.
 * Only AccountService can use these operations.
 */
@Slf4j
@UtilityClass
public final class AccountServiceAccessManager {
    
    private static final String ACCOUNT_OPERATIONS_SERVICE = AccountOperationsService.class.getName();
    /**
     * Checks if the call comes from an authorized package.
     * Uses StackTrace to verify the origin of the call.
     */
    public static void checkServiceAccess() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        boolean authorized = false;
        
        // Check if the call comes from the service package
        for (int i = 2; i < stackTrace.length; i++) { // start from 2, because 0 is getStackTrace, 1 is checkServiceAccess
            String callerClass = stackTrace[i].getClassName();
            if (callerClass.equals(ACCOUNT_OPERATIONS_SERVICE)) {
                authorized = true;
                log.debug("Access authorized for caller: {}", callerClass);
                break;
            }
        }
        
        if (!authorized) {
            String caller = stackTrace.length > 2 ? stackTrace[2].getClassName() : "unknown";
            log.warn("Unauthorized access attempt from class: {}", caller);
            throw new SecurityException("Unauthorized access to service operations");
        }
    }
}