package ada.teste.service;

import ada.teste.dto.StatementResponse;
import ada.teste.entity.ExternalStatementEntry;
import ada.teste.repository.ExternalStatementRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class ExternalStatementService {

    private static final String CACHE_NAME = "externalStatements";
    private static final String MOVEMENT_DEBIT = "DEBITO";
    private static final String MOVEMENT_CREDIT = "CREDITO";

    private final ExternalStatementRepository repository;

    public ExternalStatementService(ExternalStatementRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StatementResponse> findAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(ExternalStatementEntry::getMovementDate).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "'all'")
    @Transactional(readOnly = true)
    public List<StatementResponse> findAllCached() {
        return findAll();
    }

    @Transactional(readOnly = true)
    public List<StatementResponse> findByInstitutionId(String institutionId) {
        return repository.findByInstitutionId(institutionId).stream()
                .sorted(Comparator.comparing(ExternalStatementEntry::getMovementDate).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#institutionId")
    @Transactional(readOnly = true)
    public List<StatementResponse> findByInstitutionIdCached(String institutionId) {
        return findByInstitutionId(institutionId);
    }

    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
    public List<ExternalStatementEntry> saveAll(List<ExternalStatementEntry> entries) {
        return repository.saveAll(entries);
    }

    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
    public List<ExternalStatementEntry> saveReceivedStatements(String institutionId, List<ExternalStatementEntry> entries) {
        return repository.saveAll(entries.stream()
                .peek(entry -> entry.setInstitutionId(institutionId))
                .toList());
    }

    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
    public void deleteAll() {
        repository.deleteAll();
    }

    private StatementResponse toResponse(ExternalStatementEntry entry) {
        boolean debit = entry.getMovementValue().signum() < 0;
        return new StatementResponse(
                entry.getId(),
                entry.getInstitutionId(),
                entry.getSourceBankName(),
                entry.getSourceBankCnpj(),
                entry.getCurrentBalance(),
                entry.getMovementValue().abs(),
                debit ? MOVEMENT_DEBIT : MOVEMENT_CREDIT,
                entry.getMovementDate()
        );
    }
}

