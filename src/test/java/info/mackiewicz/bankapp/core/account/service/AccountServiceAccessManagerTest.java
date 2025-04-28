package info.mackiewicz.bankapp.core.account.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountServiceAccessManagerTest {

    @Test
    void checkServiceAccess_WhenCalledFromUnauthorizedContext_ShouldThrowSecurityException() {
        // when & then
        assertThrows(SecurityException.class, () -> {
            AccountServiceAccessManager.checkServiceAccess();
        });
    }

    @Nested
    class AuthorizedContextTest {
        
        private class MockAccountOperationsService {
            public void performAuthorizedOperation() {
                AccountServiceAccessManager.checkServiceAccess();
            }
        }

        @Test
        void checkServiceAccess_WhenCalledFromAccountOperationsService_ShouldNotThrowException() {
            // given
            MockAccountOperationsService service = new MockAccountOperationsService();

            // when & then
            assertThrows(SecurityException.class, () -> {
                service.performAuthorizedOperation();
            }, "Should throw SecurityException because MockAccountOperationsService is not the real AccountOperationsService");
        }
    }

    @Nested
    class UnauthorizedContextTest {
        
        private class UnauthorizedService {
            public void performUnauthorizedOperation() {
                AccountServiceAccessManager.checkServiceAccess();
            }
        }

        @Test
        void checkServiceAccess_WhenCalledFromUnauthorizedService_ShouldThrowSecurityException() {
            // given
            UnauthorizedService service = new UnauthorizedService();

            // when & then
            assertThrows(SecurityException.class, () -> {
                service.performUnauthorizedOperation();
            });
        }
    }

    @Test
    void checkServiceAccess_WhenCalledDirectly_ShouldThrowSecurityException() {
        // when & then
        assertThrows(SecurityException.class, () -> {
            new DirectCaller().call();
        });
    }

    private static class DirectCaller {
        public void call() {
            AccountServiceAccessManager.checkServiceAccess();
        }
    }

    @Test
    void checkServiceAccess_WhenCalledThroughMultipleLayers_ShouldThrowSecurityException() {
        // given
        LayerThree layerThree = new LayerThree();

        // when & then
        assertThrows(SecurityException.class, () -> {
            layerThree.callThroughLayers();
        });
    }

    private static class LayerOne {
        public void call() {
            AccountServiceAccessManager.checkServiceAccess();
        }
    }

    private static class LayerTwo {
        private final LayerOne layerOne = new LayerOne();

        public void call() {
            layerOne.call();
        }
    }

    private static class LayerThree {
        private final LayerTwo layerTwo = new LayerTwo();

        public void callThroughLayers() {
            layerTwo.call();
        }
    }
}