package com.caixa.ada.gravador.publisher;

import com.caixa.ada.contracts.ExtratoAtualizadoEvent;
import com.caixa.ada.gravador.configuration.KafkaConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventoPublisher {

    private final KafkaTemplate<String, ExtratoAtualizadoEvent> kafkaTemplate;

    public EventoPublisher(KafkaTemplate<String, ExtratoAtualizadoEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(ExtratoAtualizadoEvent evento) {
        kafkaTemplate.send(KafkaConfig.TOPICO_EXTRATO_ATUALIZADO, evento.clienteId(), evento);
    }
}

