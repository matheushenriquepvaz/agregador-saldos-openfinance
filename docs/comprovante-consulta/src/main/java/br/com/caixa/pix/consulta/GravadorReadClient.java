package br.com.caixa.pix.consulta;

import br.com.caixa.pix.contracts.ComprovanteView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.UUID;

/** Lê o comprovante no gravador (o "banco" do ponto de vista da consulta). */
@Component
public class GravadorReadClient {

    private final RestClient http;

    public GravadorReadClient(@Value("${gravador.base-url}") String baseUrl) {
        this.http = RestClient.create(baseUrl);
    }

    public Optional<ComprovanteView> buscar(UUID id) {
        ComprovanteView view = http.get().uri("/interno/comprovantes/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, resp) -> {}) // 404 -> corpo nulo
                .body(ComprovanteView.class);
        return Optional.ofNullable(view);
    }
}
