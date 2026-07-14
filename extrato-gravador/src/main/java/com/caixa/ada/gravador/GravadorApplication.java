package com.caixa.ada.gravador;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Extrato Gravador API", version = "0.0.1-SNAPSHOT"))
@SpringBootApplication
public class GravadorApplication {
    public static void main(String[] args) {
        SpringApplication.run(GravadorApplication.class, args);
    }
}
