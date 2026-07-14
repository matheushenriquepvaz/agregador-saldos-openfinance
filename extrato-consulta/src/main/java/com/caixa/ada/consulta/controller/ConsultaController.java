package com.caixa.ada.consulta.controller;

import com.caixa.ada.consulta.service.ConsultaService;
import com.caixa.ada.contracts.ExtratoView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/extratos")
@Tag(name = "Extratos", description = "Consulta o extrato com cache-aside e fallback no gravador")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping("/{clienteId}")
    @Operation(summary = "Buscar extrato do cliente")
    public ResponseEntity<ExtratoView> buscar(@PathVariable String clienteId) {
        ExtratoView extrato = consultaService.buscar(clienteId);
        if (extrato == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(extrato);
    }

    @DeleteMapping("/{clienteId}/cache")
    @CacheEvict(value = "extratos", key = "#clienteId")
    @Operation(summary = "Invalidar cache do extrato do cliente")
    public ResponseEntity<Void> invalidarCache(@PathVariable String clienteId) {
        return ResponseEntity.noContent().build();
    }
}
