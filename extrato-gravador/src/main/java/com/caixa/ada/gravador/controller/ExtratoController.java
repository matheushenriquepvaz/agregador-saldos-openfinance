package com.caixa.ada.gravador.controller;

import com.caixa.ada.contracts.ExtratoView;
import com.caixa.ada.gravador.service.GravadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/extratos")
@Tag(name = "Extratos", description = "Consulta o extrato consolidado no gravador")
public class ExtratoController {

    private final GravadorService gravadorService;

    public ExtratoController(GravadorService gravadorService) {
        this.gravadorService = gravadorService;
    }

    @GetMapping("/{clienteId}")
    @Operation(summary = "Buscar extrato consolidado")
    public ResponseEntity<ExtratoView> buscar(@PathVariable String clienteId) {
        Optional<ExtratoView> extrato = gravadorService.buscarExtrato(clienteId);
        return extrato.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
