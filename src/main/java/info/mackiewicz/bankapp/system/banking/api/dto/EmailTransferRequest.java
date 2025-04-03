package info.mackiewicz.bankapp.system.banking.api.dto;

import info.mackiewicz.bankapp.user.model.vo.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTransferRequest extends BankingOperationRequest {

    @Schema(description = "The destination email address for the transfer", requiredMode = RequiredMode.REQUIRED, type = "string", example = "example@user.com")
    @NotNull
    private Email destinationEmail;
}
