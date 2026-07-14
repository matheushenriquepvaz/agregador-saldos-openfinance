package com.caixa.ada.contracts;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Visão consolidada de extrato")
public record ExtratoView(
        @Schema(description = "Identificador do cliente")
        String clienteId,
        @Schema(description = "Saldo atual consolidado")
        BigDecimal saldoAtual,
        @Schema(description = "Quantidade de transações registradas")
        long quantidadeTransacoes,
        @Schema(description = "Data e hora da última atualização")
        LocalDateTime ultimaAtualizacao,
        @Schema(description = "Últimas transações do cliente")
        List<TransacaoView> transacoes
) {
}
