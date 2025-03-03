package info.mackiewicz.bankapp.account.service;

import lombok.experimental.UtilityClass;

/**
 * Klasa kontrolująca dostęp do wewnętrznych operacji konta.
 * Tylko AccountService może korzystać z tych operacji.
 */
@UtilityClass
public final class AccountServiceAccessManager {
    
    private static final String ACCOUNT_OPERATIONS_SERVICE = AccountOperationsService.class.getName();
    /**
     * Sprawdza, czy wywołanie pochodzi z dozwolonego pakietu.
     * Używa StackTrace do weryfikacji pochodzenia wywołania.
     */
    public static void checkServiceAccess() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        boolean authorized = false;
        
        // Sprawdzamy, czy wywołanie pochodzi z pakietu service
        for (int i = 2; i < stackTrace.length; i++) { // zaczynamy od 2, bo 0 to getStackTrace, 1 to checkServiceAccess
            String callerClass = stackTrace[i].getClassName();
            if (callerClass.equals(ACCOUNT_OPERATIONS_SERVICE)) {
                authorized = true;
                break;
            }
        }
        
        if (!authorized) {
            throw new SecurityException("Unauthorized access to service operations");
        }
    }
}