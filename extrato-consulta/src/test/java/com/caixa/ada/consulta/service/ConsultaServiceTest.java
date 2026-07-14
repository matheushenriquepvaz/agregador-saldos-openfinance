package com.caixa.ada.consulta.service;

import com.caixa.ada.consulta.client.GravadorClient;
import com.caixa.ada.contracts.ExtratoView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    @Mock
    private GravadorClient gravadorClient;

    @InjectMocks
    private ConsultaService service;

    @Test
    void deveRetornarExtratoNaPrimeiraTentativa() {
        ExtratoView view = new ExtratoView("cliente-1", BigDecimal.TEN, 1, LocalDateTime.now(), Collections.emptyList());
        when(gravadorClient.buscar("cliente-1")).thenReturn(Optional.of(view));

        ExtratoView result = service.buscar("cliente-1");

        assertEquals("cliente-1", result.clienteId());
        verify(gravadorClient, times(1)).buscar("cliente-1");
    }

    @Test
    void deveRetornarNullAposTresTentativasSemRegistro() {
        when(gravadorClient.buscar("cliente-2")).thenReturn(Optional.empty());

        ExtratoView result = service.buscar("cliente-2");

        assertNull(result);
        verify(gravadorClient, times(3)).buscar("cliente-2");
    }
}

