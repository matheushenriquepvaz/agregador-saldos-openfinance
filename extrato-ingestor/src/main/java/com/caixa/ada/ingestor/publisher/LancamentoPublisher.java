package com.caixa.ada.ingestor.publisher;

import com.caixa.ada.contracts.GravarLancamentoCommand;
import com.caixa.ada.ingestor.configuration.MessagingConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class LancamentoPublisher {

    private final RabbitTemplate rabbitTemplate;

    public LancamentoPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicar(GravarLancamentoCommand command) {
        rabbitTemplate.convertAndSend(MessagingConfig.FILA, command);
    }
}

