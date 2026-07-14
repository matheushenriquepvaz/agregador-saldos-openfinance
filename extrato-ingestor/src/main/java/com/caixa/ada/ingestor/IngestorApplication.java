package com.caixa.ada.ingestor;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Extrato Ingestor API", version = "0.0.1-SNAPSHOT"))
@SpringBootApplication
public class IngestorApplication {
    public static void main(String[] args) {
        SpringApplication.run(IngestorApplication.class, args);
    }
}
