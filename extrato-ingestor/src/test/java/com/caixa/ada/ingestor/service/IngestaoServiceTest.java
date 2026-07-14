package com.caixa.ada.ingestor.service;

import com.caixa.ada.contracts.GravarLancamentoCommand;
import com.caixa.ada.contracts.RecebimentoLancamentoRequest;
import com.caixa.ada.contracts.TipoLancamento;
import com.caixa.ada.ingestor.exception.LancamentoInvalidoException;
import com.caixa.ada.ingestor.publisher.LancamentoPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IngestaoServiceTest {

    @Mock
    private LancamentoPublisher publisher;

    @InjectMocks
    private IngestaoService service;

    @Test
    void devePublicarQuandoPayloadValido() {
        RecebimentoLancamentoRequest request = new RecebimentoLancamentoRequest(
                "cliente-1",
                "CAIXA",
                TipoLancamento.CREDITO,
                BigDecimal.TEN,
                "salario",
                LocalDate.now()
        );

        service.aceitar(request);

        ArgumentCaptor<GravarLancamentoCommand> captor = ArgumentCaptor.forClass(GravarLancamentoCommand.class);
        verify(publisher, times(1)).publicar(captor.capture());
        assertTrue(captor.getValue().eventoId() != null);
    }

    @Test
    void deveFalharQuandoValorInvalido() {
        RecebimentoLancamentoRequest request = new RecebimentoLancamentoRequest(
                "cliente-1",
                "CAIXA",
                TipoLancamento.DEBITO,
                BigDecimal.ZERO,
                "compra",
                LocalDate.now()
        );

        assertThrows(LancamentoInvalidoException.class, () -> service.aceitar(request));
    }
}

