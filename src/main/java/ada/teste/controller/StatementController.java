package ada.teste.controller;

import ada.teste.dto.StatementResponse;
import ada.teste.service.ExternalStatementService;
import ada.teste.service.FinbrasStatementService;
import ada.teste.service.UnifiedBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/extratos")
@Tag(name = "Extratos", description = "Consulta de extratos da Finbras e instituições externas")
public class StatementController {

    private final FinbrasStatementService finbrasStatementService;
    private final ExternalStatementService externalStatementService;
    private final UnifiedBalanceService unifiedBalanceService;

    public StatementController(FinbrasStatementService finbrasStatementService,
                               ExternalStatementService externalStatementService,
                               UnifiedBalanceService unifiedBalanceService) {
        this.finbrasStatementService = finbrasStatementService;
        this.externalStatementService = externalStatementService;
        this.unifiedBalanceService = unifiedBalanceService;
    }

    @GetMapping("/finbras")
    @Operation(summary = "Consulta extrato da Finbras sem cache")
    public List<StatementResponse> getFinbrasStatements() {
        return finbrasStatementService.findAll();
    }

    @GetMapping("/finbras/cache")
    @Operation(summary = "Consulta extrato da Finbras com cache Redis")
    public List<StatementResponse> getFinbrasStatementsCached() {
        return finbrasStatementService.findAllCached();
    }

    @GetMapping("/externos")
    @Operation(summary = "Consulta todos os extratos externos sem cache")
    public List<StatementResponse> getExternalStatements() {
        return externalStatementService.findAll();
    }

    @GetMapping("/externos/cache")
    @Operation(summary = "Consulta todos os extratos externos com cache Redis")
    public List<StatementResponse> getExternalStatementsCached() {
        return externalStatementService.findAllCached();
    }

    @GetMapping("/externos/{institutionId}")
    @Operation(summary = "Consulta extrato externo de uma instituição sem cache")
    public List<StatementResponse> getExternalStatementsByInstitution(@PathVariable String institutionId) {
        return externalStatementService.findByInstitutionId(institutionId.toUpperCase());
    }

    @GetMapping("/externos/{institutionId}/cache")
    @Operation(summary = "Consulta extrato externo de uma instituição com cache Redis")
    public List<StatementResponse> getExternalStatementsByInstitutionCached(@PathVariable String institutionId) {
        return externalStatementService.findByInstitutionIdCached(institutionId.toUpperCase());
    }

    @GetMapping("/saldo-unificado")
    @Operation(summary = "Consulta o saldo unificado da Finbras e instituições externas")
    public List<StatementResponse> getUnifiedBalance() {
        return unifiedBalanceService.findAll();
    }
}
