package com.caixa.ada.ingestor.controller;

import com.caixa.ada.contracts.LancamentoAceito;
import com.caixa.ada.contracts.RecebimentoLancamentoRequest;
import com.caixa.ada.ingestor.service.IngestaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lancamentos")
@Tag(name = "Lançamentos", description = "Recebe lançamentos e publica o evento para processamento")
public class LancamentoController {

    private final IngestaoService ingestaoService;

    public LancamentoController(IngestaoService ingestaoService) {
        this.ingestaoService = ingestaoService;
    }

    @PostMapping
    @Operation(summary = "Receber um lançamento")
    public ResponseEntity<LancamentoAceito> receber(@RequestBody RecebimentoLancamentoRequest request) {
        LancamentoAceito aceito = ingestaoService.aceitar(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(aceito);
    }
}
