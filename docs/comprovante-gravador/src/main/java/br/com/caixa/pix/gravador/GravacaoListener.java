package br.com.caixa.pix.gravador;

import br.com.caixa.pix.contracts.GravarComprovanteCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Aula 5 — consome a fila de gravação. Exceção aqui dispara o retry (config no yml);
 * esgotadas as tentativas, a mensagem é encaminhada à DLQ via dead-letter-exchange.
 */
@Component
public class GravacaoListener {

    private static final Logger log = LoggerFactory.getLogger(GravacaoListener.class);

    private final GravadorService gravador;

    public GravacaoListener(GravadorService gravador) {
        this.gravador = gravador;
    }

    @RabbitListener(queues = MessagingConfig.FILA)
    public void aoReceber(GravarComprovanteCommand command) {
        gravador.gravar(command); // idempotente sob redelivery
    }

    /** Mensagens "envenenadas" param aqui para inspeção (nunca descartadas em silêncio). */
    @RabbitListener(queues = MessagingConfig.DLQ)
    public void aoCairNaDlq(GravarComprovanteCommand command) {
        log.warn("Comprovante {} caiu na DLQ após esgotar as tentativas",
                command.identificadorComprovante());
    }
}
