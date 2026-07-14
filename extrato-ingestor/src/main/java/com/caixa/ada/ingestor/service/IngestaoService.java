package com.caixa.ada.ingestor.service;

import com.caixa.ada.contracts.GravarLancamentoCommand;
import com.caixa.ada.contracts.LancamentoAceito;
import com.caixa.ada.contracts.RecebimentoLancamentoRequest;
import com.caixa.ada.ingestor.exception.LancamentoInvalidoException;
import com.caixa.ada.ingestor.publisher.LancamentoPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class IngestaoService {

    private final LancamentoPublisher publisher;

    public IngestaoService(LancamentoPublisher publisher) {
        this.publisher = publisher;
    }

    public LancamentoAceito aceitar(RecebimentoLancamentoRequest request) {
        validar(request);
        UUID eventoId = UUID.randomUUID();
        publisher.publicar(new GravarLancamentoCommand(eventoId, request));
        return new LancamentoAceito(eventoId, LocalDateTime.now());
    }

    private void validar(RecebimentoLancamentoRequest request) {
        if (request == null) {
            throw new LancamentoInvalidoException("payload obrigatorio");
        }
        exigir(request.clienteId(), "clienteId");
        exigir(request.instituicaoId(), "instituicaoId");
        if (request.tipo() == null) {
            throw new LancamentoInvalidoException("tipo obrigatorio");
        }
        if (request.valor() == null || request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new LancamentoInvalidoException("valor deve ser maior que zero");
        }
        if (request.dataLancamento() == null) {
            throw new LancamentoInvalidoException("dataLancamento obrigatoria");
        }
    }

    private void exigir(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new LancamentoInvalidoException(campo + " obrigatorio");
        }
    }
}

