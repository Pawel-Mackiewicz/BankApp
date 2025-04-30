package info.mackiewicz.bankapp.core.account.validation;

import info.mackiewicz.bankapp.core.account.exception.UnsupportedValidationTypeException;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ExternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.shared.annotations.DifferentAccountsValidator;
import info.mackiewicz.bankapp.testutils.TestIbanProvider;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DifferentAccountsValidator ensuring correct validation
 * of source and recipient IBANs in transfer requests
 */
class DifferentAccountsValidatorTest {

    private static final String EXTERNAL_TEST_IBAN = "DE89370400440532013000";

    private DifferentAccountsValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new DifferentAccountsValidator();
    }

    @Test
    void isValid_InternalTransfer_DifferentIbans_ReturnsTrue() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setSourceIban(TestIbanProvider.getIban(0));
        request.setRecipientIban(TestIbanProvider.getIban(1));

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isValid_InternalTransfer_SameIbans_ReturnsFalse() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        String sameIban = TestIbanProvider.getIban(0);
        request.setSourceIban(sameIban);
        request.setRecipientIban(sameIban);

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isValid_ExternalTransfer_DifferentIbans_ReturnsTrue() {
        // Given
        WebTransferRequest request = new ExternalTransferRequest();
        request.setSourceIban(TestIbanProvider.getIban(0));
        request.setRecipientIban(EXTERNAL_TEST_IBAN);

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isValid_ExternalTransfer_SameIbans_ReturnsFalse() {
        // Given
        WebTransferRequest request = new ExternalTransferRequest();
        String sameIban = TestIbanProvider.getIban(0);
        request.setSourceIban(sameIban);
        request.setRecipientIban(sameIban);

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isValid_InternalTransfer_NullSourceIban_ReturnsTrue() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setRecipientIban(TestIbanProvider.getIban(0));

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isValid_InternalTransfer_NullRecipientIban_ReturnsTrue() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setSourceIban(TestIbanProvider.getIban(0));

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isValid_NotATransferRequest_ThrowsException() {
        // Given
        Object invalidRequest = new Object();

        // When/Then
        UnsupportedValidationTypeException exception = assertThrows(
                UnsupportedValidationTypeException.class,
                () -> validator.isValid(invalidRequest, context)
        );
        assertEquals("Invalid transfer request type: java.lang.Object", exception.getMessage());
    }

    @Test
    void isValid_NullValue_ReturnsTrue() {
        // When
        boolean isValid = validator.isValid(null, context);

        // Then
        assertTrue(isValid);
    }
}