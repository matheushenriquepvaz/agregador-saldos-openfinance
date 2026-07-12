package br.com.caixa.pix.notificacao;

import br.com.caixa.pix.contracts.ComprovanteGravadoEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aula 7 — chamada a um gateway externo instável, protegida por circuit breaker.
 * Aberto o circuito, o fallback evita martelar a dependência doente.
 */
@Component
public class NotificacaoGateway {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoGateway.class);

    @CircuitBreaker(name = "notificacao-gateway", fallbackMethod = "fallback")
    public void enviar(ComprovanteGravadoEvent evento) {
        // Simula chamada externa que pode falhar (rede/gateway).
        log.info("Enviando notificação do comprovante {}", evento.identificadorComprovante());
        // throw new RuntimeException("gateway indisponível"); // descomente para exercitar retry/breaker
    }

    /** Acionado quando o circuito está aberto ou a chamada falha. */
    private void fallback(ComprovanteGravadoEvent evento, Throwable causa) {
        log.warn("Fallback de notificação do comprovante {}: {}",
                evento.identificadorComprovante(), causa.getMessage());
    }
}
