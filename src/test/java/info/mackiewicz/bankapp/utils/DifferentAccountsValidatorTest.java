package info.mackiewicz.bankapp.utils;

import info.mackiewicz.bankapp.dto.ExternalTransferRequest;
import info.mackiewicz.bankapp.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.dto.TransferRequest;
import info.mackiewicz.bankapp.validation.DifferentAccountsValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class DifferentAccountsValidatorTest {

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
        request.setSourceIban("PL14485112340002210000000001");
        request.setRecipientIban("PL84485112340002210000000002");

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isValid_InternalTransfer_SameIbans_ReturnsFalse() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setSourceIban("PL14485112340002210000000001");
        request.setRecipientIban("PL14485112340002210000000001");

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isValid_ExternalTransfer_DifferentIbans_ReturnsTrue() {
        // Given
        TransferRequest request = new ExternalTransferRequest();
        request.setSourceIban("PL14485112340002210000000001");
        request.setRecipientIban("DE89370400440532013000");

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isValid_ExternalTransfer_SameIbans_ReturnsFalse() {
        // Given
        TransferRequest request = new ExternalTransferRequest();
        request.setSourceIban("PL14485112340002210000000001");
        request.setRecipientIban("PL14485112340002210000000001");

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isValid_InternalTransfer_NullSourceIban_ReturnsTrue() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setRecipientIban("PL84485112340002210000000002");

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isValid_InternalTransfer_NullRecipientIban_ReturnsTrue() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setSourceIban("PL14485112340002210000000001");

        // When
        boolean isValid = validator.isValid(request, context);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isValid_NotATransferRequest_ReturnsTrue() {
        // When
        boolean isValid = validator.isValid(new Object(), context);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isValid_NullValue_ReturnsTrue() {
        // When
        boolean isValid = validator.isValid(null, context);

        // Then
        assertTrue(isValid);
    }
}