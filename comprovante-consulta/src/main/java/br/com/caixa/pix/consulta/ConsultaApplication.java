package br.com.caixa.pix.consulta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Aula 3
public class ConsultaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultaApplication.class, args);
    }
}
