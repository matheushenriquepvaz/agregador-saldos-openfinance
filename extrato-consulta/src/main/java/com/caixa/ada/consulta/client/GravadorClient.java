package com.caixa.ada.consulta.client;

import com.caixa.ada.contracts.ExtratoView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class GravadorClient {

    private final RestTemplate restTemplate;
    private final String gravadorBaseUrl;

    public GravadorClient(RestTemplate restTemplate, @Value("${gravador.base-url:http://localhost:8082}") String gravadorBaseUrl) {
        this.restTemplate = restTemplate;
        this.gravadorBaseUrl = gravadorBaseUrl;
    }

    public Optional<ExtratoView> buscar(String clienteId) {
        String url = gravadorBaseUrl + "/extratos/" + clienteId;
        try {
            return Optional.ofNullable(restTemplate.getForObject(url, ExtratoView.class));
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw ex;
        }
    }
}

