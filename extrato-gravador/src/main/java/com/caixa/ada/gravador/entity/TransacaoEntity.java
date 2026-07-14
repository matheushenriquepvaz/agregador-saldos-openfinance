package com.caixa.ada.gravador.entity;

import com.caixa.ada.contracts.GravarLancamentoCommand;
import com.caixa.ada.contracts.TipoLancamento;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
public class TransacaoEntity {

    @Id
    private UUID eventoId;

    private String clienteId;

    private String instituicaoId;

    @Enumerated(EnumType.STRING)
    private TipoLancamento tipo;

    private BigDecimal valor;

    private String descricao;

    private LocalDate dataLancamento;

    private LocalDateTime dataProcessamento;

    protected TransacaoEntity() {
    }

    public static TransacaoEntity de(GravarLancamentoCommand command, LocalDateTime dataProcessamento) {
        TransacaoEntity entity = new TransacaoEntity();
        entity.eventoId = command.eventoId();
        entity.clienteId = command.dados().clienteId();
        entity.instituicaoId = command.dados().instituicaoId();
        entity.tipo = command.dados().tipo();
        entity.valor = command.dados().valor();
        entity.descricao = command.dados().descricao();
        entity.dataLancamento = command.dados().dataLancamento();
        entity.dataProcessamento = dataProcessamento;
        return entity;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getInstituicaoId() {
        return instituicaoId;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }
}

