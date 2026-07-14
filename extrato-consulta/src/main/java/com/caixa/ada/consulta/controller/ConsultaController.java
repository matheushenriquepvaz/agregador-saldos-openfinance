package com.caixa.ada.consulta.controller;

import com.caixa.ada.consulta.service.ConsultaService;
import com.caixa.ada.contracts.ExtratoView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/extratos")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ExtratoView> buscar(@PathVariable String clienteId) {
        ExtratoView extrato = consultaService.buscar(clienteId);
        if (extrato == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(extrato);
    }
}

