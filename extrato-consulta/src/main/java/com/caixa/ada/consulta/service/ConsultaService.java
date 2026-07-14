package com.caixa.ada.consulta.service;

import com.caixa.ada.consulta.client.GravadorClient;
import com.caixa.ada.contracts.ExtratoView;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsultaService {

    private static final int MAX_TENTATIVAS = 3;

    private final GravadorClient gravadorClient;

    public ConsultaService(GravadorClient gravadorClient) {
        this.gravadorClient = gravadorClient;
    }

    @Cacheable(value = "extratos", key = "#clienteId", unless = "#result == null")
    public ExtratoView buscar(String clienteId) {
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            Optional<ExtratoView> extrato = gravadorClient.buscar(clienteId);
            if (extrato.isPresent()) {
                return extrato.get();
            }
        }
        return null;
    }
}

