package com.caixa.ada.gravador.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "saldos_clientes")
public class SaldoClienteEntity {

    @Id
    private String clienteId;

    private BigDecimal saldoAtual;

    private long quantidadeTransacoes;

    private LocalDateTime ultimaAtualizacao;

    protected SaldoClienteEntity() {
    }

    public SaldoClienteEntity(String clienteId) {
        this.clienteId = clienteId;
        this.saldoAtual = BigDecimal.ZERO;
        this.quantidadeTransacoes = 0L;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void aplicarCredito(BigDecimal valor, LocalDateTime instante) {
        saldoAtual = saldoAtual.add(valor);
        quantidadeTransacoes++;
        ultimaAtualizacao = instante;
    }

    public void aplicarDebito(BigDecimal valor, LocalDateTime instante) {
        saldoAtual = saldoAtual.subtract(valor);
        quantidadeTransacoes++;
        ultimaAtualizacao = instante;
    }

    public String getClienteId() {
        return clienteId;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public long getQuantidadeTransacoes() {
        return quantidadeTransacoes;
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }
}

