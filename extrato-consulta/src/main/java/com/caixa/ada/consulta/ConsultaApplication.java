package com.caixa.ada.consulta;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Extrato Consulta API", version = "0.0.1-SNAPSHOT"))
@SpringBootApplication
public class ConsultaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsultaApplication.class, args);
    }
}
