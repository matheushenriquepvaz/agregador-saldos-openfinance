package ada.teste.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MockRequestResponse(
        String requestId,
        String institutionId,
        String status,
        LocalDateTime requestedAt,
        List<StatementResponse> storedStatements
) {
}

