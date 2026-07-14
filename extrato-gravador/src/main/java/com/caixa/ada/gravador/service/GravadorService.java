package com.caixa.ada.gravador.service;

import com.caixa.ada.contracts.ExtratoAtualizadoEvent;
import com.caixa.ada.contracts.ExtratoView;
import com.caixa.ada.contracts.GravarLancamentoCommand;
import com.caixa.ada.contracts.TipoLancamento;
import com.caixa.ada.contracts.TransacaoView;
import com.caixa.ada.gravador.entity.EventoProcessadoEntity;
import com.caixa.ada.gravador.entity.SaldoClienteEntity;
import com.caixa.ada.gravador.entity.TransacaoEntity;
import com.caixa.ada.gravador.publisher.EventoPublisher;
import com.caixa.ada.gravador.repository.EventoProcessadoRepository;
import com.caixa.ada.gravador.repository.SaldoClienteRepository;
import com.caixa.ada.gravador.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GravadorService {

    private final TransacaoRepository transacaoRepository;
    private final SaldoClienteRepository saldoClienteRepository;
    private final EventoProcessadoRepository eventoProcessadoRepository;
    private final EventoPublisher eventoPublisher;

    public GravadorService(
            TransacaoRepository transacaoRepository,
            SaldoClienteRepository saldoClienteRepository,
            EventoProcessadoRepository eventoProcessadoRepository,
            EventoPublisher eventoPublisher
    ) {
        this.transacaoRepository = transacaoRepository;
        this.saldoClienteRepository = saldoClienteRepository;
        this.eventoProcessadoRepository = eventoProcessadoRepository;
        this.eventoPublisher = eventoPublisher;
    }

    @Transactional
    public boolean gravar(GravarLancamentoCommand command) {
        if (eventoProcessadoRepository.existsById(command.eventoId())) {
            return false;
        }

        LocalDateTime processadoEm = LocalDateTime.now();
        TransacaoEntity transacao = TransacaoEntity.de(command, processadoEm);
        transacaoRepository.save(transacao);

        SaldoClienteEntity saldo = saldoClienteRepository
                .findById(command.dados().clienteId())
                .orElseGet(() -> new SaldoClienteEntity(command.dados().clienteId()));

        if (command.dados().tipo() == TipoLancamento.CREDITO) {
            saldo.aplicarCredito(command.dados().valor(), processadoEm);
        } else {
            saldo.aplicarDebito(command.dados().valor(), processadoEm);
        }

        saldoClienteRepository.save(saldo);
        eventoProcessadoRepository.save(new EventoProcessadoEntity(command.eventoId(), processadoEm));
        eventoPublisher.publicar(new ExtratoAtualizadoEvent(command.eventoId(), command.dados().clienteId(), processadoEm));
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<ExtratoView> buscarExtrato(String clienteId) {
        Optional<SaldoClienteEntity> saldo = saldoClienteRepository.findById(clienteId);
        if (saldo.isEmpty()) {
            return Optional.empty();
        }

        List<TransacaoView> transacoes = transacaoRepository
                .findTop10ByClienteIdOrderByDataProcessamentoDesc(clienteId)
                .stream()
                .map(this::toView)
                .toList();

        SaldoClienteEntity agregado = saldo.get();
        return Optional.of(new ExtratoView(
                clienteId,
                agregado.getSaldoAtual(),
                agregado.getQuantidadeTransacoes(),
                agregado.getUltimaAtualizacao(),
                transacoes
        ));
    }

    private TransacaoView toView(TransacaoEntity entity) {
        return new TransacaoView(
                entity.getEventoId(),
                entity.getInstituicaoId(),
                entity.getTipo(),
                entity.getValor(),
                entity.getDescricao(),
                entity.getDataLancamento(),
                entity.getDataProcessamento()
        );
    }
}

