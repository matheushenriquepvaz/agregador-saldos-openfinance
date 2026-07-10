package ada.teste.controller;

import ada.teste.dto.StatementResponse;
import ada.teste.service.UnifiedBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/saldos")
@Tag(name = "Saldos", description = "Consulta de saldo unificado da Finbras e instituições externas")
public class BalanceController {

    private final UnifiedBalanceService unifiedBalanceService;

    public BalanceController(UnifiedBalanceService unifiedBalanceService) {
        this.unifiedBalanceService = unifiedBalanceService;
    }

    @GetMapping("/unificado")
    @Operation(summary = "Consulta o saldo unificado sem cache na Finbras e com cache nas instituições externas")
    public List<StatementResponse> getUnifiedBalance() {
        return unifiedBalanceService.findAll();
    }

    @GetMapping("/unificado/{institutionId}")
    @Operation(summary = "Consulta o saldo unificado de uma instituição específica")
    public List<StatementResponse> getUnifiedBalanceByInstitution(@PathVariable String institutionId) {
        return unifiedBalanceService.findByInstitutionId(institutionId);
    }
}

