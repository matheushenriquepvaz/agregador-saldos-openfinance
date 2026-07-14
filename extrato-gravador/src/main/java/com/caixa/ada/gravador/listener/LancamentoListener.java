package com.caixa.ada.gravador.listener;

import com.caixa.ada.contracts.GravarLancamentoCommand;
import com.caixa.ada.gravador.configuration.KafkaConfig;
import com.caixa.ada.gravador.service.GravadorService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LancamentoListener {

    private final GravadorService gravadorService;

    public LancamentoListener(GravadorService gravadorService) {
        this.gravadorService = gravadorService;
    }

    @KafkaListener(topics = KafkaConfig.TOPICO_LANCAMENTOS, groupId = KafkaConfig.GRUPO_CONSUMIDOR)
    public void aoReceber(GravarLancamentoCommand command) {
        gravadorService.gravar(command);
    }
}

