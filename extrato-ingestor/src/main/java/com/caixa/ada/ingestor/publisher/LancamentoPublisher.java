package com.caixa.ada.ingestor.publisher;

import com.caixa.ada.contracts.GravarLancamentoCommand;
import com.caixa.ada.ingestor.configuration.KafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LancamentoPublisher {

    private final KafkaTemplate<String, GravarLancamentoCommand> kafkaTemplate;

    public LancamentoPublisher(KafkaTemplate<String, GravarLancamentoCommand> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(GravarLancamentoCommand command) {
        kafkaTemplate.send(KafkaConfig.TOPICO_LANCAMENTOS, command.dados().clienteId(), command);
    }
}

