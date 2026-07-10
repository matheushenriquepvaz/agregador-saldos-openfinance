package ada.teste.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StatementResponse(
        Long id,
        String institutionId,
        String sourceBankName,
        String sourceBankCnpj,
        BigDecimal currentBalance,
        BigDecimal movementValue,
        String movementType,
        LocalDateTime movementDate
) {
}

