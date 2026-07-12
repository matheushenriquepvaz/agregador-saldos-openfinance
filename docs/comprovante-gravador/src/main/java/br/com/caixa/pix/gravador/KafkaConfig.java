package br.com.caixa.pix.gravador;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Aula 6 — tópico de eventos de domínio. */
@Configuration
public class KafkaConfig {

    public static final String TOPICO = "comprovante-gravado";

    @Bean
    public NewTopic comprovanteGravadoTopic() {
        return TopicBuilder.name(TOPICO).partitions(3).replicas(1).build();
    }
}
