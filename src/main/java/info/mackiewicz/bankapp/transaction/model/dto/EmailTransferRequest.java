package info.mackiewicz.bankapp.transaction.model.dto;

import info.mackiewicz.bankapp.user.model.vo.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTransferRequest extends BankingOperationRequest {

    @Schema(description = "The destination email address for the transfer")
    @NotNull
    private Email destinationEmail;
}
