package com.caixa.ada.gravador.service;

import com.caixa.ada.contracts.GravarLancamentoCommand;
import com.caixa.ada.contracts.RecebimentoLancamentoRequest;
import com.caixa.ada.contracts.TipoLancamento;
import com.caixa.ada.gravador.entity.SaldoClienteEntity;
import com.caixa.ada.gravador.publisher.EventoPublisher;
import com.caixa.ada.gravador.repository.EventoProcessadoRepository;
import com.caixa.ada.gravador.repository.SaldoClienteRepository;
import com.caixa.ada.gravador.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GravadorServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private SaldoClienteRepository saldoClienteRepository;

    @Mock
    private EventoProcessadoRepository eventoProcessadoRepository;

    @Mock
    private EventoPublisher eventoPublisher;

    @InjectMocks
    private GravadorService service;

    @Test
    void deveIgnorarEventoDuplicado() {
        GravarLancamentoCommand command = novoCommand(TipoLancamento.CREDITO, BigDecimal.TEN);
        when(eventoProcessadoRepository.existsById(command.eventoId())).thenReturn(true);

        boolean processado = service.gravar(command);

        assertFalse(processado);
        verify(transacaoRepository, never()).save(any());
        verify(eventoPublisher, never()).publicar(any());
    }

    @Test
    void deveAtualizarSaldoQuandoNovoEvento() {
        GravarLancamentoCommand command = novoCommand(TipoLancamento.DEBITO, BigDecimal.ONE);
        when(eventoProcessadoRepository.existsById(command.eventoId())).thenReturn(false);
        when(saldoClienteRepository.findById("cliente-1")).thenReturn(Optional.empty());

        boolean processado = service.gravar(command);

        assertTrue(processado);
        ArgumentCaptor<SaldoClienteEntity> saldoCaptor = ArgumentCaptor.forClass(SaldoClienteEntity.class);
        verify(saldoClienteRepository).save(saldoCaptor.capture());
        assertEquals(new BigDecimal("-1"), saldoCaptor.getValue().getSaldoAtual());
        assertEquals(1L, saldoCaptor.getValue().getQuantidadeTransacoes());
    }

    private GravarLancamentoCommand novoCommand(TipoLancamento tipo, BigDecimal valor) {
        return new GravarLancamentoCommand(
                UUID.randomUUID(),
                new RecebimentoLancamentoRequest(
                        "cliente-1",
                        "CAIXA",
                        tipo,
                        valor,
                        "descricao",
                        LocalDate.now()
                )
        );
    }
}

