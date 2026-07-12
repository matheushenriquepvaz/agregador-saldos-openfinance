package br.com.caixa.pix.notificacao;

import br.com.caixa.pix.contracts.ComprovanteGravadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

/**
 * Aula 7 — consumo resiliente: @RetryableTopic cria tópicos de retry com backoff e,
 * esgotadas as tentativas, encaminha para a DLT. A chamada externa tem circuit breaker.
 */
@Component
public class NotificacaoListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoListener.class);

    private final NotificacaoGateway gateway;

    public NotificacaoListener(NotificacaoGateway gateway) {
        this.gateway = gateway;
    }

    @RetryableTopic(attempts = "4", backoff = @Backoff(delay = 1000, multiplier = 2.0))
    @KafkaListener(topics = "comprovante-gravado", groupId = "notificacao")
    public void aoGravar(ComprovanteGravadoEvent evento) {
        gateway.enviar(evento);
    }

    @DltHandler
    public void aoCairNaDlt(ComprovanteGravadoEvent evento) {
        log.error("Comprovante {} caiu na DLT do tópico de notificação", evento.identificadorComprovante());
    }
}
