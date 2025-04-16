package info.mackiewicz.bankapp.system.banking.operations.transfer;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.interfaces.AccountServiceInterface;
import info.mackiewicz.bankapp.system.banking.operations.api.dto.TransferResponse;
import info.mackiewicz.bankapp.system.banking.operations.service.TransferOperationService;
import info.mackiewicz.bankapp.system.banking.operations.service.helpers.AccountSecurityService;
import info.mackiewicz.bankapp.system.banking.operations.service.helpers.TransactionBuildingService;
import info.mackiewicz.bankapp.testutils.TestAccountBuilder;
import info.mackiewicz.bankapp.testutils.TestIbanProvider;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class BaseTransferServiceTest {

    @Mock
    protected AccountServiceInterface accountService;

    @Mock
    protected TransactionBuildingService transactionBuilderService;

    @Mock
    protected AccountSecurityService accountSecurityService;

    @Mock
    protected TransactionService transactionService;

    @Mock
    protected TransferOperationService operationsService;

    @Mock
    protected UserDetailsWithId userDetails;

    @Captor
    protected ArgumentCaptor<Transaction> transactionCaptor;

    protected Account sourceAccount;
    protected Account destinationAccount;
    protected Transaction transaction;
    protected static final Integer USER_ID = 1;
    protected static final Iban SOURCE_IBAN = TestIbanProvider.getNextIbanObject();
    protected static final Iban DEST_IBAN = TestIbanProvider.getNextIbanObject();
    protected static final BigDecimal TRANSFER_AMOUNT = BigDecimal.valueOf(100);
    protected static final String TRANSFER_TITLE = "Test transfer";

    @BeforeEach
    void setUp() {
        User owner1 = TestUserBuilder.createRandomTestUser();
        User owner2 = TestUserBuilder.createRandomTestUser();
        sourceAccount = TestAccountBuilder.createTestAccount(1, TRANSFER_AMOUNT, owner1);
        destinationAccount = TestAccountBuilder.createTestAccount(2, TRANSFER_AMOUNT, owner2);
        transaction = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(TRANSFER_AMOUNT)
                .withTitle(TRANSFER_TITLE)
                .asInternalTransfer()
                .build();

        when(userDetails.getId()).thenReturn(USER_ID);
    }

    protected void assertSuccessfulTransfer(TransferResponse response) {
        assertThat(response).isNotNull();
        assertThat(response.getSourceAccount().getFormattedIban()).isEqualTo(sourceAccount.getFormattedIban());
        assertThat(response.getTargetAccount().getFormattedIban()).isEqualTo(destinationAccount.getFormattedIban());
        assertThat(response.getTransactionInfo().getAmount()).isEqualTo(transaction.getAmount());
        assertThat(response.getTransactionInfo().getTitle()).isEqualTo(transaction.getTitle());
    }
}