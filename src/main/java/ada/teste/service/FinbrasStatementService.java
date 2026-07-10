package ada.teste.service;

import ada.teste.dto.StatementResponse;
import ada.teste.entity.FinbrasStatementEntry;
import ada.teste.repository.FinbrasStatementRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class FinbrasStatementService {

    private static final String CACHE_NAME = "finbrasStatements";
    private static final String MOVEMENT_DEBIT = "DEBITO";
    private static final String MOVEMENT_CREDIT = "CREDITO";

    private final FinbrasStatementRepository repository;

    public FinbrasStatementService(FinbrasStatementRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StatementResponse> findAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(FinbrasStatementEntry::getMovementDate).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "'all'")
    @Transactional(readOnly = true)
    public List<StatementResponse> findAllCached() {
        return findAll();
    }

    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
    public List<FinbrasStatementEntry> saveAll(List<FinbrasStatementEntry> entries) {
        return repository.saveAll(entries);
    }

    @Transactional
    @CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
    public void deleteAll() {
        repository.deleteAll();
    }

    private StatementResponse toResponse(FinbrasStatementEntry entry) {
        boolean debit = entry.getMovementValue().signum() < 0;
        return new StatementResponse(
                entry.getId(),
                "FINBRAS",
                "",
                null,
                entry.getCurrentBalance(),
                entry.getMovementValue().abs(),
                debit ? MOVEMENT_DEBIT : MOVEMENT_CREDIT,
                entry.getMovementDate()
        );
    }
}

