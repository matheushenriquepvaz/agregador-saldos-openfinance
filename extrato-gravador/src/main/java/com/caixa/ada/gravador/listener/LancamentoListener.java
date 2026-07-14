package com.caixa.ada.gravador.listener;

import com.caixa.ada.contracts.GravarLancamentoCommand;
import com.caixa.ada.gravador.configuration.MessagingConfig;
import com.caixa.ada.gravador.service.GravadorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class LancamentoListener {

    private final GravadorService gravadorService;

    public LancamentoListener(GravadorService gravadorService) {
        this.gravadorService = gravadorService;
    }

    @RabbitListener(queues = MessagingConfig.FILA)
    public void aoReceber(GravarLancamentoCommand command) {
        gravadorService.gravar(command);
    }
}

