package br.com.caixa.pix.gravador;

import br.com.caixa.pix.contracts.ComprovanteGravadoEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** Publica o FATO "comprovante gravado" no tópico (Aula 6). Chave = id (ordenação por comprovante). */
@Component
public class EventoPublisher {

    private final KafkaTemplate<String, ComprovanteGravadoEvent> kafka;

    public EventoPublisher(KafkaTemplate<String, ComprovanteGravadoEvent> kafka) {
        this.kafka = kafka;
    }

    public void publicar(ComprovanteGravadoEvent evento) {
        kafka.send(KafkaConfig.TOPICO, evento.identificadorComprovante().toString(), evento);
    }
}
