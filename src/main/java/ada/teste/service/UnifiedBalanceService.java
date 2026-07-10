package ada.teste.service;

import ada.teste.dto.StatementResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class UnifiedBalanceService {

    private static final String CACHE_NAME = "unifiedBalances";

    private final FinbrasStatementService finbrasStatementService;
    private final ExternalStatementService externalStatementService;

    public UnifiedBalanceService(FinbrasStatementService finbrasStatementService,
                                 ExternalStatementService externalStatementService) {
        this.finbrasStatementService = finbrasStatementService;
        this.externalStatementService = externalStatementService;
    }

    @Transactional(readOnly = true)
    public List<StatementResponse> findAll() {
        List<StatementResponse> balance = new ArrayList<>();
        balance.addAll(finbrasStatementService.findAll());
        balance.addAll(externalStatementService.findAllCached());
        return balance.stream()
                .sorted(Comparator.comparing(StatementResponse::movementDate).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StatementResponse> findByInstitutionId(String institutionId) {
        String normalizedInstitutionId = institutionId == null ? null : institutionId.trim().toUpperCase();
        if (normalizedInstitutionId == null || normalizedInstitutionId.isBlank() || "FINBRAS".equals(normalizedInstitutionId)) {
            return finbrasStatementService.findAll();
        }
        return externalStatementService.findByInstitutionIdCached(normalizedInstitutionId);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "'all'")
    @Transactional(readOnly = true)
    public List<StatementResponse> findAllCached() {
        return findAll();
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#institutionId")
    @Transactional(readOnly = true)
    public List<StatementResponse> findByInstitutionIdCached(String institutionId) {
        return findByInstitutionId(institutionId);
    }
}
