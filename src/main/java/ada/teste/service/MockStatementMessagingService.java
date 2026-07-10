package ada.teste.service;

import ada.teste.dto.MockMessageRequest;
import ada.teste.dto.MockRequestResponse;
import ada.teste.dto.StatementResponse;
import ada.teste.entity.ExternalStatementEntry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MockStatementMessagingService {

    private final ExternalStatementService externalStatementService;

    public MockStatementMessagingService(ExternalStatementService externalStatementService) {
        this.externalStatementService = externalStatementService;
    }

    public MockRequestResponse requestUpdatedStatements(MockMessageRequest request) {
        String institutionId = request == null ? null : request.institutionId();
        List<String> institutions = resolveInstitutions(institutionId);
        List<StatementResponse> stored = new ArrayList<>();

        for (String institution : institutions) {
            List<ExternalStatementEntry> entries = buildMockEntries(institution);
            externalStatementService.saveReceivedStatements(institution, entries);
            stored.addAll(externalStatementService.findByInstitutionId(institution));
        }

        return new MockRequestResponse(
                UUID.randomUUID().toString(),
                institutionId == null || institutionId.isBlank() ? "ALL_EXTERNAL_INSTITUTIONS" : institutionId,
                "RECEIVED_AND_PERSISTED",
                LocalDateTime.now(),
                stored
        );
    }

    private List<String> resolveInstitutions(String institutionId) {
        if (institutionId == null || institutionId.isBlank()) {
            return List.of("BANCO_ALPHA", "BANCO_BETA", "BANCO_GAMMA");
        }
        return List.of(institutionId.trim().toUpperCase());
    }

    private List<ExternalStatementEntry> buildMockEntries(String institutionId) {
        return switch (institutionId) {
            case "BANCO_ALPHA" -> List.of(
                    new ExternalStatementEntry(institutionId, "Banco Alpha", "12.345.678/0001-90", new BigDecimal("1800.00"), new BigDecimal("-100.00"), LocalDateTime.now().minusMinutes(5)),
                    new ExternalStatementEntry(institutionId, "Banco Alpha", "12.345.678/0001-90", new BigDecimal("1900.00"), new BigDecimal("200.00"), LocalDateTime.now().minusMinutes(2))
            );
            case "BANCO_BETA" -> List.of(
                    new ExternalStatementEntry(institutionId, "Banco Beta", "98.765.432/0001-10", new BigDecimal("2400.00"), new BigDecimal("-50.00"), LocalDateTime.now().minusMinutes(6)),
                    new ExternalStatementEntry(institutionId, "Banco Beta", "98.765.432/0001-10", new BigDecimal("2450.00"), new BigDecimal("120.00"), LocalDateTime.now().minusMinutes(1))
            );
            default -> List.of(
                    new ExternalStatementEntry(institutionId, "Banco Gamma", "11.222.333/0001-44", new BigDecimal("600.00"), new BigDecimal("-20.00"), LocalDateTime.now().minusMinutes(4)),
                    new ExternalStatementEntry(institutionId, "Banco Gamma", "11.222.333/0001-44", new BigDecimal("620.00"), new BigDecimal("40.00"), LocalDateTime.now().minusMinutes(1))
            );
        };
    }
}

