package info.mackiewicz.bankapp.system.banking.history.controller;

import info.mackiewicz.bankapp.system.banking.shared.dto.TransactionResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;

/**
 * A wrapper class extending Spring's PageImpl for documenting paginated TransactionResponse objects in OpenAPI/Swagger.
 *
 * <p>Used solely for providing accurate API schema representation with proper generic type information.</p>
 *
 * @see TransactionResponse
 * @see org.springframework.data.domain.Page
 */
@Schema(name = "PageOfTransactions", description = "Paginated collection of transaction records")
public class PageTransactionResponse extends PageImpl<TransactionResponse> {

    /**
     * Default constructor creating an empty page for documentation purposes.
     */
    public PageTransactionResponse() {
        super(Collections.emptyList());
    }
}
